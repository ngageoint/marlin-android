package mil.nga.msi.geopackage.export

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.msi.datasource.DataSource
import mil.nga.sf.Geometry
import java.lang.UnsupportedOperationException

data class FeatureData(
   val name: String,
   val value: Any?
)

interface Feature {
   val geometry: Geometry
   val values: List<FeatureData>
}

data class FeatureColumn(
   val name: String,
   val title: String,
   val type: GeoPackageDataType
)

interface DataSourceDefinition {
   val tableName: String
   val icon: Int
   val columns: List<FeatureColumn>

   companion object {
      fun fromDataSource(dataSource: DataSource): DataSourceDefinition {
         return when (dataSource) {
            DataSource.ASAM -> AsamDefinition()
            DataSource.MODU -> ModuDefinition()
            DataSource.PORT -> PortDefinition()
            else -> throw UnsupportedOperationException()
         }
      }
   }
}