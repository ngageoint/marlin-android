package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.buildZoomNavOptions
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

sealed class RadioBeaconAction : Action() {
   class Tap(private val radioBeacon: RadioBeacon): RadioBeaconAction() {
      override fun navigate(navController: NavController) {
         val key = RadioBeaconKey.fromRadioBeacon(radioBeacon)
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${RadioBeaconRoute.Detail.name}?key=$encoded")
      }
   }

   class Zoom(private val latLng: LatLng): RadioBeaconAction() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}", buildZoomNavOptions(navController))
      }
   }

   class Share(val radioBeacon: RadioBeacon): RadioBeaconAction()
   class Location(val text: String): RadioBeaconAction()
}