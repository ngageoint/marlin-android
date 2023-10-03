package mil.nga.msi.geopackage.export

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.Point

class ModuDefinition() : DataSourceDefinition {
   override val tableName = "modus"
   override val icon = MapAnnotation.Type.MODU.icon
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("date", "Date", GeoPackageDataType.DATE),
      FeatureColumn("name", "Name", GeoPackageDataType.TEXT),
      FeatureColumn("latitude", "Latitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("longitude", "Longitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("position", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("rigStatus", "Rig Status", GeoPackageDataType.TEXT),
      FeatureColumn("specialStatus", "Special Status", GeoPackageDataType.TEXT),
      FeatureColumn("distance", "Description", GeoPackageDataType.DOUBLE),
      FeatureColumn("navigationArea", "Navigation Area", GeoPackageDataType.TEXT),
      FeatureColumn("region", "Region", GeoPackageDataType.TEXT),
      FeatureColumn("subregion", "Subregion", GeoPackageDataType.TEXT)
      )
}

class ModuFeature(
   modu: Modu
) : Feature {
   override val geometry = Point(modu.longitude, modu.latitude)

   override val values = listOf(
      FeatureData("date", modu.date),
      FeatureData("name", modu.name),
      FeatureData("latitude", modu.latitude),
      FeatureData("longitude", modu.longitude),
      FeatureData("position", MGRS.from(modu.longitude, modu.latitude).coordinate()),
      FeatureData("rigStatus", modu.rigStatus.toString()),
      FeatureData("specialStatus", modu.specialStatus),
      FeatureData("distance", modu.distance),
      FeatureData("navigationArea", modu.navigationArea),
      FeatureData("region", modu.region),
      FeatureData("subregion", modu.subregion),
   )
}