package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint

sealed class DgpsStationAction : Action() {
   class Tap(val dgpsStation: DgpsStation): DgpsStationAction() {
      override fun navigate(navController: NavController) {
         val key = DgpsStationKey.fromDgpsStation(dgpsStation)
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${DgpsStationRoute.Detail.name}?key=$encoded")
      }
   }

   class Zoom(val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}")
      }
   }

   class Share(val dgpsStation: DgpsStation): DgpsStationAction()
   class Location(val text: String): DgpsStationAction()
}