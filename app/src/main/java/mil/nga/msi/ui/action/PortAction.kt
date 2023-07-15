package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.port.PortRoute

sealed class PortAction: Action() {
   class Tap(val port: Port): PortAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${PortRoute.Detail.name}?portNumber=${port.portNumber}")
      }
   }

   class Zoom(val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}")
      }
   }

   class Share(val port: Port): PortAction()
   class Location(val text: String): PortAction()
}