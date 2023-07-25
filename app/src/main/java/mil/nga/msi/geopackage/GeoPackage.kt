package mil.nga.msi.geopackage

import com.google.android.gms.maps.model.LatLng
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.attributes.AttributesRow
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.related.ExtendedRelation
import mil.nga.geopackage.extension.related.RelatedTablesExtension
import mil.nga.geopackage.extension.related.RelationType
import mil.nga.geopackage.extension.schema.columns.DataColumnsDao
import mil.nga.msi.datasource.bookmark.Bookmark

data class GeoPackageFeature(
   val id: Long,
   val name: String,
   val table: String,
   val latLng: LatLng? = null,
   val properties: List<GeoPackageFeatureProperty> = emptyList(),
   val attributes: List<GeoPackageFeatureAttribute> = emptyList(),
   val bookmark: Bookmark? = null
)

open class GeoPackageFeatureProperty(
   val key: String,
   open val value: Any
)

data class GeoPackageFeatureAttribute(
   val properties: List<GeoPackageFeatureProperty>
)

class GeoPackageMediaProperty(
   key: String,
   override val value: ByteArray,
   val mediaTable: String,
   val mediaId: Long,
   val contentType: String
): GeoPackageFeatureProperty(key, value)

fun GeoPackage.getFeature(
   layerId: Long,
   layerName: String,
   table: String,
   featureId: Long
): GeoPackageFeature? {
   return try {
      val featureDao = getFeatureDao(table)
      val relatedTablesExtension = RelatedTablesExtension(this)
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
         val properties: MutableList<GeoPackageFeatureProperty> = ArrayList()
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
                  properties.add(GeoPackageMediaProperty(name, mediaRow.data, mediaDao.tableName, mediaRow.id, contentType))
               }
            }
         }

         for (relation in attributeTables) {
            val relatedAttributes = relatedTablesExtension.getMappingsForBase(
               relation.mappingTableName,
               featureId
            )
            val attributesDao = getAttributesDao(relation.relatedTableName)
            for (relatedAttribute in relatedAttributes) {
               val row = attributesDao.queryForIdRow(relatedAttribute)
               if (row != null) {
                  attributeRows.add(row)
               }
            }
         }

         val dataColumnsDao = DataColumnsDao.create(this)
         val latLng = featureRow.geometry?.geometry?.centroid?.let { point ->
            LatLng(point.y, point.x)
         }

         for (i in 0 until featureRow.columnCount()) {
            val value = featureRow.getValue(i)
            var columnName = featureRow.getColumnName(i)
            if (dataColumnsDao.isTable) {
               val dataColumn = dataColumnsDao.getDataColumn(featureRow.table.tableName, columnName)
               if (dataColumn != null) {
                  columnName = dataColumn.name
               }
            }

            if (value != null && featureRow.columns.getColumn(i).dataType != GeoPackageDataType.BLOB) {
               properties.add(GeoPackageFeatureProperty(columnName, value))
            }
         }

         val attributes: MutableList<GeoPackageFeatureAttribute> = ArrayList()
         for (row in attributeRows) {
            val attributeProperties: MutableList<GeoPackageFeatureProperty> = ArrayList()
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
                     attributeProperties.add(GeoPackageMediaProperty(name, mediaRow.data, mediaDao.tableName, mediaRow.id, contentType))
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
                  attributeProperties.add(GeoPackageFeatureProperty(columnName, value))
               }
            }
            attributes.add(GeoPackageFeatureAttribute(attributeProperties))
         }

         GeoPackageFeature(
            id = layerId,
            name = layerName,
            table = table,
            latLng = latLng,
            properties = properties,
            attributes = attributes
         )
      }
   } catch (e: Exception) { null }
}