package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.buildZoomNavOptions
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint

sealed class LightAction : Action() {
   class Tap(val light: Light): LightAction() {
      override fun navigate(navController: NavController) {
         val key = LightKey.fromLight(light)
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${LightRoute.Detail.name}?key=$encoded")
      }
   }

   class Zoom(val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}", buildZoomNavOptions(navController))
      }
   }

   class Location(val text: String): LightAction()
   class Share(val light: Light) : LightAction()
}