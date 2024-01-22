package mil.nga.msi.datasource

import androidx.compose.ui.graphics.Color
import mil.nga.msi.R
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.bookmark.BookmarkRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.electronicpublication.ElectronicPublicationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.route.list.RouteRoute

data class Tab(
   val route: Route,
   val title: String
)

enum class DataSource(
   val tab: Tab? = null,
   val mappable: Boolean,
   val color: Color,
   val icon: Int,
   val label: String,
   val labelPlural: String = "${label}s",
   val tableName: String? = null,
   val route: Route? = null
) {
   ASAM(
      tab = Tab(AsamRoute.Main, "ASAMs"),
      mappable = true,
      color = Color(0xFF000000),
      icon = R.drawable.ic_asam_24dp,
      label = "ASAM",
      tableName = "asams",
      route = AsamRoute.Main
   ),

   MODU(
      tab = Tab(ModuRoute.Main, "MODUs"),
      mappable = true,
      color = Color(0xFF0042A4),
      icon = R.drawable.ic_modu_24dp,
      label = "MODU",
      tableName = "modus"
   ),

   NAVIGATION_WARNING(
      tab = Tab(NavigationWarningRoute.Main, "Warnings"),
      mappable = true,
      color = Color(0xFFD32F2F),
      icon = R.drawable.ic_round_warning_24,
      label = "Navigational Warning",
      tableName = "navigational_warnings",
   ),

   LIGHT(
      tab = Tab(LightRoute.Main, "Lights"),
      mappable = true,
      color = Color(0xFFFFC500),
      icon = R.drawable.ic_baseline_lightbulb_24,
      label = "Light",
      tableName = "lights",
   ),

   PORT(
      tab = Tab(PortRoute.Main, "Ports"),
      mappable = true,
      color = Color(0xFF5856D6),
      icon = R.drawable.ic_baseline_anchor_24,
      label = "World Port",
      tableName = "ports"
   ),

   RADIO_BEACON(
      tab = Tab(RadioBeaconRoute.Main, "Beacons"),
      mappable = true,
      color = Color(0xFF007BFF),
      icon = R.drawable.ic_baseline_settings_input_antenna_24,
      label = "Radio Beacon",
      tableName = "radio_beacons",
   ),

   DGPS_STATION(
      tab = Tab(DgpsStationRoute.Main, "DGPS"),
      mappable = true,
      color = Color(0xFF00E676),
      icon = R.drawable.ic_dgps_icon_24,
      label = "Differential GPS Station",
      tableName = "dgps_stations"
   ),

   ELECTRONIC_PUBLICATION(
      tab = Tab(ElectronicPublicationRoute.Main, "EPUB"),
      mappable = false,
      color = Color(0xFF30B0C7),
      icon = R.drawable.ic_description_24dp,
      label = "Electronic Publication",
      tableName = "epubs"
   ),

   NOTICE_TO_MARINERS(
      tab = Tab(NoticeToMarinersRoute.Main, "NTM"),
      mappable = false,
      color = Color(0xFFFF0000),
      icon = R.drawable.ic_baseline_campaign_24,
      label = "Notice To Mariners",
      tableName = "notice_to_mariners"
   ),

   BOOKMARK(
      tab = Tab(BookmarkRoute.Main, "Bookmarks"),
      mappable = false,
      color = Color(0xFFFF9500),
      icon = R.drawable.ic_outline_bookmark_border_24,
      label = "Bookmarks",
   ),

   ROUTE(
      tab = Tab(RouteRoute.Main, "Routes"),
      mappable = true,
      color = Color(0xFF000000),
      icon = R.drawable.ic_outline_directions_24dp,
      label = "Routes"
   ),

   ROUTE_WAYPOINT(
      mappable = false,
      color = Color(0xFF000000),
      icon = R.drawable.ic_outline_directions_24dp,
      label = "Route Waypoint"
   ),

   GEOPACKAGE(
      mappable = false,
      color = Color(0xFFA2855E),
      icon = R.drawable.ic_round_place_24,
      label = "GeoPackage Feature"
   );

   fun labelForCount(count: Int) = if (count == 1) label else labelPlural
}