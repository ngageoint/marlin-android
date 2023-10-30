package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.ui.geopackage.GeoPackageRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint

sealed class GeoPackageFeatureAction : Action() {
   class Tap(private val key: GeoPackageFeatureKey): GeoPackageFeatureAction() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate(GeoPackageRoute.Detail.name + "?key=${encoded}")
      }
   }

   class Zoom(private val latLng: LatLng): Action() {
      override fun navigate(navController: NavController) {
         val point = NavPoint(latLng.latitude, latLng.longitude)
         val encoded = Uri.encode(Json.encodeToString(point))
         navController.navigate(MapRoute.Map.name + "?point=${encoded}")
      }
   }

   class Location(val text: String): GeoPackageFeatureAction()
   data object Media : GeoPackageFeatureAction()
}
