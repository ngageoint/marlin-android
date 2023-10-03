package mil.nga.msi.geopackage.export

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.color.Color
import mil.nga.geopackage.BoundingBox
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.db.TableColumnKey
import mil.nga.geopackage.extension.nga.style.FeatureStyleExtension
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.IconRow
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.geopackage.extension.schema.SchemaExtension
import mil.nga.geopackage.extension.schema.columns.DataColumns
import mil.nga.geopackage.features.columns.GeometryColumns
import mil.nga.geopackage.features.user.FeatureColumn
import mil.nga.geopackage.features.user.FeatureTable
import mil.nga.geopackage.features.user.FeatureTableMetadata
import mil.nga.geopackage.geom.GeoPackageGeometryData
import mil.nga.msi.datasource.DataSource
import mil.nga.proj.ProjectionConstants
import mil.nga.sf.GeometryType
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.io.path.createDirectories

data class ExportStatus(
   val total: Int,
   val complete: Int
)

class Export @Inject constructor(
   private val application: Application,
   private val geoPackageManager: GeoPackageManager
) {

   suspend fun export(
      items: Map<DataSource, List<Feature>>,
      onStatus: (Map<DataSource, ExportStatus>) -> Unit
   ) = withContext(Dispatchers.IO) {
      val status = items.map { (dataSource, features) ->
         dataSource to ExportStatus(features.size, 0)
      }.toMap().toMutableMap()

      try {
         // TODO handle errors
         create()?.let { geoPackage ->
            items.forEach { (dataSource, features) ->
               val table = createTable(
                  definition = DataSourceDefinition.fromDataSource(dataSource),
                  geoPackage = geoPackage
               )
               val tableStyles = FeatureTableStyles(geoPackage, table)
               val styles = createStyles(tableStyles)

               createFeatures(geoPackage, table, styles, features) { complete ->
                  status[dataSource] = ExportStatus(features.size, complete)
                  onStatus(status)
               }
            }

            File(geoPackage.path)
         }
      } catch(e: Exception) {
         Log.e("Billy", "nope", e)
         null
      }
   }

   private fun create(): GeoPackage? {
      val file = Paths.get(application.cacheDir.absolutePath, "exports")
         .createDirectories()
         .resolve("marlin_export_${dateFormat.format(Date())}.gpkg")
         .toFile()

      return if (geoPackageManager.createFile(file)) {
         geoPackageManager.openExternal(file)
      } else null
   }

   private fun createTable(
      definition: DataSourceDefinition,
      geoPackage: GeoPackage
   ): FeatureTable {
      val srs = geoPackage.spatialReferenceSystemDao.getOrCreateCode(ProjectionConstants.AUTHORITY_EPSG, ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM_GEOGRAPHICAL_3D.toLong())

      val geometryColumns = GeometryColumns()
      geometryColumns.tableName = definition.tableName
      geometryColumns.columnName = "geometry"
      geometryColumns.setGeometryType(GeometryType.GEOMETRY)
      geometryColumns.z = 0
      geometryColumns.m = 0
      geometryColumns.setSrs(srs)

      val columns = mutableListOf<FeatureColumn>()
      val dataColumns = mutableListOf<DataColumns>()

      definition.columns.forEach { column ->
         columns.add(
            FeatureColumn.createColumn(
               column.name,
               column.type
            )
         )
      }

      // for now.  Calculate this properly at some point
      val boundingBox = BoundingBox(-180.0, -90.0, 180.0, 90.0)
      val featureTableMetadata = FeatureTableMetadata(geometryColumns, "object_id", true, columns, boundingBox)
      val featureTable = geoPackage.createFeatureTable(featureTableMetadata)

      // TODO needs to be data source dependent

      definition.columns.forEach { column ->
         dataColumns.add(DataColumns().apply {
            id = TableColumnKey(definition.tableName, column.name)
            name = column.name
            title = column.title
            contents = geoPackage.getFeatureDao(featureTable).contents
         })
      }

      // create the data columns extension for human readable column names
      SchemaExtension(geoPackage).createDataColumnsTable()
      SchemaExtension.getDataColumnsDao(geoPackage)?.let { dao ->
         dataColumns.forEach { dao.create(it) }
      }

      // add the icon
      val style = FeatureStyleExtension(geoPackage)
      style.createStyleTable()
      style.createIconTable()

      val styleDao = style.styleDao
      val iconDao = style.iconDao

      // TODO needs to be data source dependent
      val color = DataSource.ASAM.color

      val featureTableStyles = FeatureTableStyles(geoPackage, featureTable)
      val tableStyleDefault = styleDao.newRow()
      tableStyleDefault.setName("${definition.tableName} Style")
      tableStyleDefault.setColor(Color(color.red, color.green, color.blue))
      tableStyleDefault.setFillColor(Color(color.red, color.green, color.blue))
      tableStyleDefault.opacity = color.alpha.toDouble()
      tableStyleDefault.setFillOpacity(0.3)
      tableStyleDefault.setWidth(2.0)
      featureTableStyles.setTableStyleDefault(tableStyleDefault)

      val bitmap = ContextCompat.getDrawable(application, definition.icon)?.toBitmap()!!

      val stream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
      val imageData = stream.toByteArray()

      val iconStyleDefault: IconRow = iconDao.newRow()
      iconStyleDefault.isTableIcon = true
      iconStyleDefault.name = "Icon"
      iconStyleDefault.contentType = "image/png"
      iconStyleDefault.data = imageData
      iconStyleDefault.width = 30.0
      iconStyleDefault.height = 30.0
      featureTableStyles.setTableIconDefault(iconStyleDefault)

      return featureTable
   }

   private fun createStyles(
      tableStyles: FeatureTableStyles
   ): List<StyleRow> {
      return emptyList()
   }

   private fun createFeatures(
      geoPackage: GeoPackage,
      table: FeatureTable,
      styles: List<StyleRow>,
      features: List<Feature>,
      onStatus: (Int) -> Unit
   ) {
      features.forEachIndexed { index, feature ->
         createFeature(geoPackage, table, feature)
         onStatus(index + 1)
      }
   }

   private fun createFeature(
      geoPackage: GeoPackage,
      table: FeatureTable,
      feature: Feature
   ) {
      val featureDao = geoPackage.getFeatureDao(table)
      val row = featureDao.newRow()
      row.geometry = GeoPackageGeometryData(feature.geometry)

      feature.values.forEach { (columnName, value) ->
         row.setValue(columnName, value)
      }

      featureDao.create(row)
   }

   companion object {
      val dateFormat = SimpleDateFormat("yMMddHHmmss", Locale.US)
   }
}