package mil.nga.msi.geopackage.export

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.Geometry
import mil.nga.sf.geojson.FeatureConverter
import mil.nga.sf.geojson.GeometryCollection

class NavigationalWarningDefinition() : DataSourceDefinition {
   override val tableName = "navigational_warnings"
   override val icon = MapAnnotation.Type.NAVIGATIONAL_WARNING.icon
   override val color = DataSource.NAVIGATION_WARNING.color
   override fun getStyles(tableStyles: FeatureTableStyles) = emptyList<StyleRow>()
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("number", "Number", GeoPackageDataType.INT),
      FeatureColumn("date", "Date", GeoPackageDataType.DATE),
      FeatureColumn("year", "Reference", GeoPackageDataType.INT),
      FeatureColumn("navigationArea", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("text", "Navigation Area", GeoPackageDataType.TEXT),
      FeatureColumn("subregions", "Subregion", GeoPackageDataType.TEXT),
      FeatureColumn("status", "Status", GeoPackageDataType.TEXT),
      FeatureColumn("authority", "Authority", GeoPackageDataType.TEXT),
      FeatureColumn("cancelNumber", "Victim", GeoPackageDataType.INT),
      FeatureColumn("cancelDate", "Cancel Date", GeoPackageDataType.DATE),
      FeatureColumn("cancelYear", "Cancel Year", GeoPackageDataType.INT),
      FeatureColumn("cancelNavigationArea", "Cancel Navigation Area", GeoPackageDataType.TEXT)
   )
}

class NavigationalWarningFeature(
   private val warning: NavigationalWarning
) : Feature {

   override val geometry: Geometry?
      get() {
         return try {
            warning.geoJson?.let { geoJson ->
               val geometries = FeatureConverter.toFeatureCollection(geoJson).features.map { feature ->
                  FeatureConverter.toGeometry(feature.geometry)
               }
               GeometryCollection(geometries).geometry
            }
         } catch (e: Exception) {
            null
         }

      }

   override val values: List<FeatureData> = listOf(
      FeatureData("number", warning.number),
      FeatureData("date", warning.issueDate),
      FeatureData("year", warning.year),
      FeatureData("navigationArea", warning.navigationArea.toString()),
      FeatureData("text", warning.text),
      FeatureData("subregions", warning.subregions?.joinToString(", ")),
      FeatureData("status", warning.status),
      FeatureData("authority", warning.authority),
      FeatureData("cancelNumber", warning.cancelNumber),
      FeatureData("cancelDate", warning.cancelDate),
      FeatureData("cancelYear", warning.cancelYear),
      FeatureData("cancelNavigationArea", warning.cancelNavigationArea)
   )
}