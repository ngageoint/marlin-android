package mil.nga.msi.ui.geopackage

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.attributes.AttributesRow
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.related.ExtendedRelation
import mil.nga.geopackage.extension.related.RelatedTablesExtension
import mil.nga.geopackage.extension.related.RelationType
import mil.nga.geopackage.extension.schema.columns.DataColumnsDao
import mil.nga.geopackage.geom.GeoPackageGeometryData
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.sf.proj.GeometryTransform
import javax.inject.Inject

class Feature(
   val id: Long,
   val name: String,
   val table: String,
   val latLng: LatLng? = null,
   val properties: List<FeatureProperty> = emptyList(),
   val attributes: List<FeatureAttribute> = emptyList()
)

open class FeatureProperty(
   val key: String,
   open val value: Any
)

data class FeatureAttribute(
   val properties: List<FeatureProperty>
)

class MediaProperty(
   key: String,
   override val value: ByteArray,
   val mediaTable: String,
   val mediaId: Long,
   val contentType: String
): FeatureProperty(key, value)

@HiltViewModel
class GeoPackageViewModel @Inject constructor(
   private val application: Application,
   private val repository: LayerRepository,
   private val geoPackageManager: GeoPackageManager
): ViewModel() {
   private val _layer = MutableLiveData<Layer>()
   val layer: LiveData<Layer> = _layer

   fun setLayer(layerId: Long) {
      viewModelScope.launch {
         _layer.postValue(repository.getLayer(layerId))
      }
   }

   private val _tileProvider = MutableLiveData<TileProvider>()
   val tileProvider: LiveData<TileProvider> = _tileProvider

   private val _feature = MutableLiveData<Feature>()
   val feature: LiveData<Feature> = _feature

   fun setFeature(layerId: Long, table: String, featureId: Long) {
      viewModelScope.launch(Dispatchers.IO) {
         repository.getLayer(layerId)?.let { layer ->
            try {
               val geoPackage = geoPackageManager.openExternal(layer.filePath)
               getFeature(geoPackage, layer.id, layer.name, table, featureId)?.let {
                  _feature.postValue(it)
               }

               val featureDao = geoPackage.getFeatureDao(table)
               val featureTiles = mil.nga.geopackage.tiles.features.DefaultFeatureTiles(
                  application,
                  geoPackage,
                  featureDao
               )
               _tileProvider.postValue(FeatureOverlay(featureTiles))

            } catch (_: Exception) { }
         }
      }
   }

   fun setMedia(key: GeoPackageMediaKey) {
      viewModelScope.launch(Dispatchers.IO) {
         repository.getLayer(key.layerId)?.let { layer ->
            val geoPackage = geoPackageManager.openExternal(layer.filePath)
            _media.postValue(getMedia(geoPackage, key.table, key.mediaId))
         }
      }
   }

   private fun getFeature(
      geoPackage: GeoPackage,
      layerId: Long,
      layerName: String,
      table: String,
      featureId: Long
   ): Feature? {
      return try {
         val featureDao = geoPackage.getFeatureDao(table)
         val relatedTablesExtension = RelatedTablesExtension(geoPackage)
         val relationsDao = relatedTablesExtension.extendedRelationsDao
         val mediaTables: MutableList<ExtendedRelation> = ArrayList()
         val attributeTables: MutableList<ExtendedRelation> = ArrayList()
         if (relationsDao.isTableExists) {
            mediaTables.addAll(relationsDao.getBaseTableRelations(table)
               .filter { relation ->
                  relation.relationType == RelationType.MEDIA
               }
            )

            attributeTables.addAll(relationsDao.getBaseTableRelations(table)
               .filter { relation ->
                  relation.relationType == RelationType.ATTRIBUTES ||
                  relation.relationType == RelationType.SIMPLE_ATTRIBUTES
               }
            )
         }

         featureDao.queryForIdRow(featureId)?.let { featureRow ->
            val properties: MutableList<FeatureProperty> = ArrayList()
            val attributeRows: MutableList<AttributesRow> = ArrayList()

            for (relation in mediaTables) {
               val relatedMedia = relatedTablesExtension.getMappingsForBase(relation.mappingTableName, featureId)
               val mediaDao = relatedTablesExtension.getMediaDao(relation.relatedTableName)
               val mediaRows = mediaDao.getRows(relatedMedia)
               for (mediaRow in mediaRows) {
                  var name = "Media"
                  var columnIndex = mediaRow.columns.getColumnIndex("title", false)
                  if (columnIndex == null) {
                     columnIndex = mediaRow.columns.getColumnIndex("name", false)
                  }
                  if (columnIndex != null) {
                     name = mediaRow.getValue(columnIndex).toString()
                  }

                  val typeIndex = mediaRow.columns.getColumnIndex("content_type", false)
                  if (typeIndex != null) {
                     val contentType = mediaRow.getValue(typeIndex).toString()
                     properties.add(MediaProperty(name, mediaRow.data, mediaDao.tableName, mediaRow.id, contentType))
                  }
               }
            }

            for (relation in attributeTables) {
               val relatedAttributes = relatedTablesExtension.getMappingsForBase(
                  relation.mappingTableName,
                  featureId
               )
               val attributesDao = geoPackage.getAttributesDao(relation.relatedTableName)
               for (relatedAttribute in relatedAttributes) {
                  val row = attributesDao.queryForIdRow(relatedAttribute)
                  if (row != null) {
                     attributeRows.add(row)
                  }
               }
            }

            val dataColumnsDao = DataColumnsDao.create(geoPackage)
            var latLng: LatLng? = null
            val geometryColumn = featureRow.geometryColumnIndex
            for (i in 0 until featureRow.columnCount()) {
               val value = featureRow.getValue(i)
               var columnName = featureRow.getColumnName(i)
               if (dataColumnsDao.isTable) {
                  val dataColumn =
                     dataColumnsDao.getDataColumn(featureRow.table.tableName, columnName)
                  if (dataColumn != null) {
                     columnName = dataColumn.name
                  }
               }

               if (i == geometryColumn) {
                  val geometryData = value as GeoPackageGeometryData
                  var centroid = geometryData.geometry.centroid
                  val transform = GeometryTransform.create(featureDao.projection, 4326L)
                  centroid = transform.transform(centroid)
                  latLng = LatLng(centroid.y, centroid.x)
               }

               if (value != null && featureRow.columns.getColumn(i).dataType != GeoPackageDataType.BLOB) {
                  properties.add(FeatureProperty(columnName, value))
               }
            }

            val attributes: MutableList<FeatureAttribute> = ArrayList()
            for (row in attributeRows) {
               val attributeProperties: MutableList<FeatureProperty> = ArrayList()
               val attributeId = row.id
               for (relation in mediaTables) {
                  val relatedMedia = relatedTablesExtension.getMappingsForBase(
                     relation.mappingTableName,
                     attributeId
                  )

                  val mediaDao = relatedTablesExtension.getMediaDao(relation.relatedTableName)
                  val mediaRows = mediaDao.getRows(relatedMedia)
                  for (mediaRow in mediaRows) {
                     var name = "Media"
                     var columnIndex = mediaRow.columns.getColumnIndex("title", false)
                     if (columnIndex == null) {
                        columnIndex = mediaRow.columns.getColumnIndex("name", false)
                     }
                     if (columnIndex != null) {
                        name = mediaRow.getValue(columnIndex).toString()
                     }

                     val typeIndex = mediaRow.columns.getColumnIndex("content_type", false)
                     if (typeIndex != null) {
                        val contentType = mediaRow.getValue(typeIndex).toString()
                        attributeProperties.add(MediaProperty(name, mediaRow.data, mediaDao.tableName, mediaRow.id, contentType))
                     }
                  }
               }

               for (i in 0 until row.columnCount()) {
                  val value = row.getValue(i)
                  var columnName = row.getColumnName(i)
                  if (dataColumnsDao.isTable) {
                     val dataColumn = dataColumnsDao.getDataColumn(row.table.tableName, columnName)
                     if (dataColumn != null) {
                        columnName = dataColumn.name
                     }
                  }

                  if (value != null && row.columns.getColumn(i).dataType != GeoPackageDataType.BLOB) {
                     attributeProperties.add(FeatureProperty(columnName, value))
                  }
               }
               attributes.add(FeatureAttribute(attributeProperties))
            }

            Feature(
               id = layerId,
               name = layerName,
               table = table,
               latLng = latLng,
               properties = properties,
               attributes = attributes
            )
         }
      } catch (_: Exception) { null }
   }

   private val _media = MutableLiveData<MediaProperty>()
   val media: LiveData<MediaProperty> = _media

   private fun getMedia(
      geoPackage: GeoPackage,
      table: String,
      mediaId: Long
   ): MediaProperty? {
      val relatedTablesExtension = RelatedTablesExtension(geoPackage)
      val mediaDao = relatedTablesExtension.getMediaDao(table)
      val mediaRow = mediaDao.getRow(mediaDao.queryForIdRow(mediaId))
      return mediaRow.columns.getColumnIndex("content_type", false)?.let { index ->
         val contentType = mediaRow.getValue(index).toString()
         MediaProperty("Media", mediaRow.data, mediaDao.tableName, mediaRow.id, contentType)
      }
   }
}