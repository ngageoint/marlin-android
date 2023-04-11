package mil.nga.msi.ui.map.cluster

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.R
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.geopackage.GeoPackageRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

@Serializable
@Parcelize
data class MapAnnotation(
   val key: Key,
   val latitude: Double,
   val longitude: Double
) : Parcelable {
   enum class Type constructor(val route: Route, val icon : Int) {
      ASAM(AsamRoute.Main, R.drawable.ic_asam_marker_24dp),
      MODU(ModuRoute.Main, R.drawable.ic_modu_marker_24dp),
      LIGHT(LightRoute.Main, R.drawable.ic_light_marker_24dp),
      PORT(PortRoute.Main, R.drawable.ic_port_marker_24dp),
      RADIO_BEACON(RadioBeaconRoute.Main, R.drawable.ic_beacon_marker_24dp),
      DGPS_STATION(DgpsStationRoute.Main, R.drawable.ic_dgps_marker_24dp),
      GEOPACKAGE(GeoPackageRoute.Sheet, R.drawable.ic_feature_marker_24dp)
   }

   @Serializable
   @Parcelize
   data class Key(
      val id: String,
      val type: Type
   ) : Parcelable
}