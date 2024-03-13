package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.maps.android.SphericalUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.buildZoomNavOptions
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigation.NavPoint

sealed class ModuAction : Action() {
   class Tap(val modu: Modu): ModuAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${ModuRoute.Detail.name}?name=${modu.name}")
      }
   }

   class Zoom(val modu: Modu): Action() {
      override fun navigate(navController: NavController) {
         val radius = modu.distance
         if (radius == null) {
            val point = NavPoint(modu.latLng.latitude, modu.latLng.longitude)
            val encoded = Uri.encode(Json.encodeToString(point))
            navController.navigate(MapRoute.Map.name + "?point=${encoded}", buildZoomNavOptions(navController))
         } else {
            val radiusInMeters = radius * 1852
            val northBound = SphericalUtil.computeOffset(modu.latLng, radiusInMeters, 0.0)
            val eastBound = SphericalUtil.computeOffset(modu.latLng, radiusInMeters, 90.0)
            val southBound = SphericalUtil.computeOffset(modu.latLng, radiusInMeters, 180.0)
            val westBound = SphericalUtil.computeOffset(modu.latLng, radiusInMeters, 270.0)
            val bounds = Bounds(
               southBound.latitude,
               westBound.longitude,
               northBound.latitude,
               eastBound.longitude
            )
            val encodedBounds = Uri.encode(Json.encodeToString(bounds))
            navController.navigate(MapRoute.Map.name + "?bounds=${encodedBounds}", buildZoomNavOptions(navController))
         }
      }
   }

   class Location(val text: String): ModuAction()
   class Share(val modu: Modu) : ModuAction()
}