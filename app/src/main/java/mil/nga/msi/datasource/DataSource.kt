package mil.nga.msi.datasource

import androidx.compose.ui.graphics.Color
import mil.nga.msi.R
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

enum class DataSource(
   val route: Route,
   val mappable: Boolean,
   val color: Color,
   val icon: Int
) {
   ASAM(AsamRoute.Main, true, Color(0xFF000000), R.drawable.ic_asam_24dp),
   MODU(ModuRoute.Main,true, Color(0xFF0042A4), R.drawable.ic_modu_24dp),
   NAVIGATION_WARNING(NavigationWarningRoute.Main, false, Color(0xFFD32F2F), R.drawable.ic_round_warning_24),
   LIGHT(LightRoute.Main, true, Color(0xFFFFC500), R.drawable.ic_baseline_lightbulb_24),
   PORT(PortRoute.Main, true, Color(0xFF5856d6), R.drawable.ic_baseline_anchor_24),
   RADIO_BEACON(RadioBeaconRoute.Main, true, Color(0xFF007BFF), R.drawable.ic_baseline_settings_input_antenna_24),
   DGPS_STATION(DgpsStationRoute.Main, true, Color(0xFFFFB300), R.drawable.ic_baseline_settings_input_antenna_24)
}