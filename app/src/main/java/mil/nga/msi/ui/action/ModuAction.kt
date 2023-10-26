package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.export.ExportRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.NavPoint

sealed class ModuAction(): Action() {
   class Tap(val modu: Modu): ModuAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${ModuRoute.Detail.name}?name=${modu.name}")
      }
   }

   class Zoom(val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}")
      }
   }

   class Location(val text: String): ModuAction()
   class Share(val modu: Modu) : ModuAction()
}