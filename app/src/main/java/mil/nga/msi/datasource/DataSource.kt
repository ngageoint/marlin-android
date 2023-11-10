package mil.nga.msi.datasource

import androidx.compose.ui.graphics.Color
import mil.nga.msi.R

enum class DataSource(
   val tab: Boolean,
   val mappable: Boolean,
   val color: Color,
   val icon: Int,
   val label: String,
   val labelPlural: String = "${label}s",
   val tableName: String? = null
) {
   ASAM(true, true, Color(0xFF000000), R.drawable.ic_asam_24dp, label = "ASAM", tableName = "asams"),
   MODU(true, true, Color(0xFF0042A4), R.drawable.ic_modu_24dp, label = "MODU", tableName = "modus"),
   NAVIGATION_WARNING(true, true, Color(0xFFD32F2F), R.drawable.ic_round_warning_24, label = "Navigational Warning", tableName = "navigational_warnings"),
   LIGHT(true, true, Color(0xFFFFC500), R.drawable.ic_baseline_lightbulb_24, label = "Light", tableName = "lights"),
   PORT(true, true, Color(0xFF5856D6), R.drawable.ic_baseline_anchor_24, label = "World Port", tableName = "ports"),
   RADIO_BEACON(true, true, Color(0xFF007BFF), R.drawable.ic_baseline_settings_input_antenna_24, label = "Radio Beacon", tableName = "radio_beacons"),
   DGPS_STATION(true, true, Color(0xFF00E676), R.drawable.ic_dgps_icon_24, label = "Differential GPS Station", tableName = "dgps_stations"),
   ELECTRONIC_PUBLICATION(true, false, Color(0xFF30B0C7), R.drawable.ic_description_24dp, label = "Electronic Publication", tableName = "epubs"),
   NOTICE_TO_MARINERS(true, false, Color(0xFFFF0000), R.drawable.ic_baseline_campaign_24, label = "Notice To Mariners", tableName = "notice_to_mariners"),
   GEOPACKAGE(false, false, Color(0xFFA2855E), R.drawable.ic_round_place_24, label = "GeoPackage Feature"),
   BOOKMARK(true, false, Color(0xFFFF9500), R.drawable.ic_outline_bookmark_border_24, label = "Bookmarks"),
   ROUTE(true, true, Color(0xFF000000), R.drawable.ic_outline_directions_24dp, label = "Routes");
   fun labelForCount(count: Int) = if (count == 1) label else labelPlural
}