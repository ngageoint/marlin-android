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
import mil.nga.geopackage.extension.schema.SchemaExtension
import mil.nga.geopackage.extension.schema.columns.DataColumns
import mil.nga.geopackage.features.columns.GeometryColumns
import mil.nga.geopackage.features.index.FeatureIndexManager
import mil.nga.geopackage.features.index.FeatureIndexType
import mil.nga.geopackage.features.user.FeatureColumn
import mil.nga.geopackage.features.user.FeatureTable
import mil.nga.geopackage.features.user.FeatureTableMetadata
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
      onStatus: (Map<DataSource, ExportStatus>) -> Unit,
      onError: () -> Unit
   ) = withContext(Dispatchers.IO) {
      val status = items.map { (dataSource, features) ->
         dataSource to ExportStatus(features.size, 0)
      }.toMap().toMutableMap()

      try {
         create()?.let { geoPackage ->
            items.forEach { (dataSource, features) ->
               val definition =  DataSourceDefinition.fromDataSource(dataSource)
               val table = createTable(
                  definition = definition,
                  geoPackage = geoPackage
               )
               val tableStyles = FeatureTableStyles(geoPackage, table)
               val styleRows = definition.getStyles(tableStyles)

               features.forEachIndexed { index, feature ->
                  feature.createFeature(geoPackage, table, styleRows)
                  status[dataSource] = ExportStatus(features.size, index + 1)
                  onStatus(status)
               }

               FeatureIndexManager(application, geoPackage, geoPackage.getFeatureDao(table)).apply {
                  indexLocation = FeatureIndexType.GEOPACKAGE
               }.index()
            }

            File(geoPackage.path)
         }
      } catch(e: Exception) {
         Log.e(LOG_NAME, "Error creating GeoPackage", e)
         onError()
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
               column.key,
               column.type
            )
         )
      }

      // for now.  Calculate this properly at some point
      val boundingBox = BoundingBox(-180.0, -90.0, 180.0, 90.0)
      val featureTableMetadata = FeatureTableMetadata(geometryColumns, "object_id", true, columns, boundingBox)
      val featureTable = geoPackage.createFeatureTable(featureTableMetadata)

      definition.columns.forEach { column ->
         dataColumns.add(DataColumns().apply {
            id = TableColumnKey(definition.tableName, column.key)
            name = column.key
            title = column.title
            contents = geoPackage.getFeatureDao(featureTable).contents
         })
      }

      // Create the data columns extension for human readable column names
      SchemaExtension(geoPackage).createDataColumnsTable()
      SchemaExtension.getDataColumnsDao(geoPackage)?.let { dao ->
         dataColumns.forEach { dao.create(it) }
      }

      val style = FeatureStyleExtension(geoPackage)
      style.createStyleTable()
      style.createIconTable()

      val featureTableStyles = FeatureTableStyles(geoPackage, featureTable)
      val styleDao = style.styleDao
      val tableStyleDefault = styleDao.newRow()
      tableStyleDefault.setName("${definition.tableName} Style")
      tableStyleDefault.setColor(Color(definition.color.red, definition.color.green, definition.color.blue))
      tableStyleDefault.setFillColor(Color(definition.color.red, definition.color.green, definition.color.blue))
      tableStyleDefault.opacity = definition.color.alpha.toDouble()
      tableStyleDefault.setFillOpacity(0.3)
      tableStyleDefault.setWidth(2.0)
      featureTableStyles.setTableStyleDefault(tableStyleDefault)

      val bitmap = ContextCompat.getDrawable(application, definition.icon)?.toBitmap()!!

      val stream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
      val imageData = stream.toByteArray()

      val iconDao = style.iconDao
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

   companion object {
      private val LOG_NAME = Export::class.java.simpleName
      private val dateFormat = SimpleDateFormat("yMMddHHmmss", Locale.US)
   }
}