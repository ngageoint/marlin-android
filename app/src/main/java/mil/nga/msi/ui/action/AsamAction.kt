package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint

sealed class AsamAction(): Action() {
   class Tap(private val asam: Asam): AsamAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${AsamRoute.Detail.name}?reference=${asam.reference}")
      }
   }

   class Zoom(private val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}")
      }
   }

   class Share(val asam: Asam): AsamAction()
   class Location(val text: String): AsamAction()
}

