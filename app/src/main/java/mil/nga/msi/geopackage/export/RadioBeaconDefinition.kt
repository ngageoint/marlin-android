package mil.nga.msi.geopackage.export

import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.geopackage.extension.nga.style.FeatureTableStyles
import mil.nga.geopackage.extension.nga.style.StyleRow
import mil.nga.mgrs.MGRS
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.sf.Point

class RadioBeaconDefinition() : DataSourceDefinition {
   override val tableName = "radio_beacons"
   override val icon = MapAnnotation.Type.RADIO_BEACON.icon
   override val color = DataSource.RADIO_BEACON.color
   override fun getStyles(tableStyles: FeatureTableStyles) = emptyList<StyleRow>()
   override val columns: List<FeatureColumn> = listOf(
      FeatureColumn("name", "Name", GeoPackageDataType.TEXT),
      FeatureColumn("latitude", "Latitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("longitude", "Longitude", GeoPackageDataType.DOUBLE),
      FeatureColumn("position", "Position", GeoPackageDataType.TEXT),
      FeatureColumn("volumeNumber", "Volume Number", GeoPackageDataType.TEXT),
      FeatureColumn("featureNumber", "Feature Number", GeoPackageDataType.TEXT),
      FeatureColumn("noticeNumber", "Notice Number", GeoPackageDataType.TEXT),
      FeatureColumn("noticeWeek", "Notice Week", GeoPackageDataType.TEXT),
      FeatureColumn("noticeYear", "Notice Year", GeoPackageDataType.TEXT),
      FeatureColumn("aidType", "Aid Type", GeoPackageDataType.TEXT),
      FeatureColumn("geopoliticalHeading", "Geopolitical Heading", GeoPackageDataType.TEXT),
      FeatureColumn("regionHeading", "Region Heading", GeoPackageDataType.TEXT),
      FeatureColumn("precedingNote", "Preceding Note", GeoPackageDataType.TEXT),
      FeatureColumn("characteristic", "Characteristic", GeoPackageDataType.TEXT),
      FeatureColumn("range", "Range", GeoPackageDataType.TEXT),
      FeatureColumn("sequenceText", "Sequence Text", GeoPackageDataType.TEXT),
      FeatureColumn("frequency", "Frequency", GeoPackageDataType.TEXT),
      FeatureColumn("stationRemark", "Station Remark", GeoPackageDataType.TEXT),
      FeatureColumn("postNote", "Post Note", GeoPackageDataType.TEXT),
      FeatureColumn("deleteFlag", "Delete Flag", GeoPackageDataType.TEXT),
      FeatureColumn("removeFromList", "Remove From List", GeoPackageDataType.TEXT)
   )
}

class RadioBeaconFeature(
   beacon: RadioBeacon
) : Feature {
   override val geometry = Point(beacon.longitude, beacon.latitude)

   override val values = listOf(
      FeatureData("name", beacon.name),
      FeatureData("latitude", beacon.latitude),
      FeatureData("longitude", beacon.longitude),
      FeatureData("position", MGRS.from(beacon.longitude, beacon.latitude).coordinate()),
      FeatureData("volumeNumber", beacon.volumeNumber),
      FeatureData("featureNumber", beacon.featureNumber),
      FeatureData("noticeNumber", beacon.noticeNumber),
      FeatureData("noticeWeek", beacon.noticeWeek),
      FeatureData("noticeYear", beacon.noticeYear),
      FeatureData("aidType", beacon.aidType),
      FeatureData("geopoliticalHeading", beacon.geopoliticalHeading),
      FeatureData("regionHeading", beacon.regionHeading),
      FeatureData("precedingNote", beacon.precedingNote),
      FeatureData("characteristic", beacon.characteristic),
      FeatureData("range", beacon.range),
      FeatureData("sequenceText", beacon.sequenceText),
      FeatureData("frequency", beacon.frequency),
      FeatureData("stationRemark", beacon.stationRemark),
      FeatureData("postNote", beacon.postNote),
      FeatureData("deleteFlag", beacon.deleteFlag),
      FeatureData("removeFromList", beacon.removeFromList)
   )
}