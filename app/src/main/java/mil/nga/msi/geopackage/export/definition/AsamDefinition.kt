package mil.nga.msi.geopackage.export.definition

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.Point

class AsamDefinition : DataSourceDefinition {
   override val tableName = "asams"
   override val icon = MapAnnotation.Type.ASAM.icon
   override val color = DataSource.ASAM.color
   override fun getStyles(tableStyles: FeatureTableStyles) = emptyList<StyleRow>()
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("date", "Date", GeoPackageDataType.DATE),
      FeatureColumn("reference", "Reference", GeoPackageDataType.TEXT),
      FeatureColumn("latitude", "Latitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("longitude", "Longitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("position", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("navigationArea", "Navigation Area", GeoPackageDataType.TEXT),
      FeatureColumn("subregion", "Subregion", GeoPackageDataType.TEXT),
      FeatureColumn("description", "Description", GeoPackageDataType.TEXT),
      FeatureColumn("hostility", "Hostility", GeoPackageDataType.TEXT),
      FeatureColumn("victim", "Victim", GeoPackageDataType.TEXT),
   )
}

class AsamFeature(
   asam: Asam
) : Feature {
   override val geometry = Point(asam.longitude, asam.latitude)

   override val values: List<FeatureData> = listOf(
      FeatureData("date", asam.date),
      FeatureData("reference", asam.reference),
      FeatureData("latitude", asam.latitude),
      FeatureData("longitude",  asam.longitude),
      FeatureData("position", MGRS.from(asam.longitude, asam.latitude).coordinate()),
      FeatureData("navigationArea", asam.navigationArea),
      FeatureData("subregion", asam.subregion),
      FeatureData("description", asam.description),
      FeatureData("hostility", asam.hostility),
      FeatureData("victim", asam.victim)
   )
}