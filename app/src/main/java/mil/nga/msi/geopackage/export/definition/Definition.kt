package mil.nga.msi.geopackage.export.definition

import androidx.compose.ui.graphics.Color
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.geopackage.features.user.FeatureTable
import mil.nga.geopackage.geom.GeoPackageGeometryData
import mil.nga.msi.datasource.DataSource
import mil.nga.sf.Geometry
import java.lang.UnsupportedOperationException

data class FeatureData(
   val name: String,
   val value: Any?
)

interface Feature {
   val geometry: Geometry?
   val values: List<FeatureData>

   fun createFeature(
      geoPackage: GeoPackage,
      table: FeatureTable,
      styleRows: List<StyleRow>
   ) {
      val featureDao = geoPackage.getFeatureDao(table)
      val row = featureDao.newRow()
      row.geometry = GeoPackageGeometryData(geometry)

      values.forEach { (columnName, value) ->
         row.setValue(columnName, value)
      }

      featureDao.create(row)
   }
}

data class FeatureColumn(
   val key: String,
   val title: String,
   val type: GeoPackageDataType
)

interface DataSourceDefinition {
   val tableName: String
   val icon: Int
   val color: Color
   val columns: List<FeatureColumn>

   fun getStyles(tableStyles: FeatureTableStyles): List<StyleRow>

   companion object {
      fun fromDataSource(dataSource: DataSource): DataSourceDefinition {
         return when (dataSource) {
            DataSource.ASAM -> AsamDefinition()
            DataSource.DGPS_STATION -> DgpsStationDefinition()
            DataSource.LIGHT -> LightDefinition()
            DataSource.MODU -> ModuDefinition()
            DataSource.NAVIGATION_WARNING -> NavigationalWarningDefinition()
            DataSource.PORT -> PortDefinition()
            DataSource.RADIO_BEACON -> RadioBeaconDefinition()
            else -> throw UnsupportedOperationException()
         }
      }
   }
}