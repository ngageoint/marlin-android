package mil.nga.msi.geopackage.export.definition

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.geopackage.export.definition.DataSourceDefinition
import mil.nga.msi.geopackage.export.definition.Feature
import mil.nga.msi.geopackage.export.definition.FeatureColumn
import mil.nga.msi.geopackage.export.definition.FeatureData
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.Point

class DgpsStationDefinition() : DataSourceDefinition {
   override val tableName = "dgps_stations"
   override val icon = MapAnnotation.Type.DGPS_STATION.icon
   override val color = DataSource.DGPS_STATION.color
   override fun getStyles(tableStyles: FeatureTableStyles) = emptyList<StyleRow>()
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("name", "Name", GeoPackageDataType.TEXT),
      FeatureColumn("latitude", "Latitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("longitude", "Longitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("position", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("volumeNumber", "Volume Number", GeoPackageDataType.TEXT),
      FeatureColumn("featureNumber", "Feature Number", GeoPackageDataType.FLOAT),
      FeatureColumn("noticeNumber", "Notice Number", GeoPackageDataType.INT),
      FeatureColumn("noticeWeek", "Notice Week", GeoPackageDataType.TEXT),
      FeatureColumn("noticeYear", "Notice Year", GeoPackageDataType.TEXT),
      FeatureColumn("stationId", "Station ID", GeoPackageDataType.TEXT),
      FeatureColumn("aidType", "Aid Type", GeoPackageDataType.TEXT),
      FeatureColumn("geopoliticalHeading", "Geopolitical Heading", GeoPackageDataType.TEXT),
      FeatureColumn("regionHeading", "Region Heading", GeoPackageDataType.TEXT),
      FeatureColumn("precedingNote", "Preceding Note", GeoPackageDataType.TEXT),
      FeatureColumn("range", "Range", GeoPackageDataType.INT),
      FeatureColumn("frequency", "Frequency", GeoPackageDataType.FLOAT),
      FeatureColumn("transferRate", "Transfer Rate", GeoPackageDataType.INT),
      FeatureColumn("remarks", "Remarks", GeoPackageDataType.TEXT),
      FeatureColumn("postNote", "Post Note", GeoPackageDataType.TEXT),
      FeatureColumn("deleteFlag", "Delete Flag", GeoPackageDataType.TEXT),
      FeatureColumn("removeFromList", "Remove From List", GeoPackageDataType.TEXT)
   )
}

class DgpsStationFeature(
   dgpsStation: DgpsStation
) : Feature {
   override val geometry = Point(dgpsStation.longitude, dgpsStation.latitude)

   override val values = listOf(
      FeatureData("name", dgpsStation.name),
      FeatureData("latitude", dgpsStation.latitude),
      FeatureData("longitude", dgpsStation.longitude),
      FeatureData("position", MGRS.from(dgpsStation.longitude, dgpsStation.latitude).coordinate()),
      FeatureData("volumeNumber", dgpsStation.volumeNumber),
      FeatureData("featureNumber", dgpsStation.featureNumber),
      FeatureData("noticeNumber", dgpsStation.noticeNumber),
      FeatureData("noticeWeek", dgpsStation.noticeWeek),
      FeatureData("noticeYear", dgpsStation.noticeYear),
      FeatureData("stationId", dgpsStation.stationId),
      FeatureData("aidType", dgpsStation.aidType),
      FeatureData("geopoliticalHeading", dgpsStation.geopoliticalHeading),
      FeatureData("regionHeading", dgpsStation.regionHeading),
      FeatureData("precedingNote", dgpsStation.precedingNote),
      FeatureData("range", dgpsStation.range),
      FeatureData("frequency", dgpsStation.frequency),
      FeatureData("transferRate", dgpsStation.transferRate),
      FeatureData("remarks", dgpsStation.remarks),
      FeatureData("postNote", dgpsStation.postNote),
      FeatureData("deleteFlag", dgpsStation.deleteFlag),
      FeatureData("removeFromList", dgpsStation.removeFromList)
   )
}