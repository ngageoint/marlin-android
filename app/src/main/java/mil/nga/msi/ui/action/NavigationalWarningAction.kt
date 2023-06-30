package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute

sealed class NavigationalWarningAction(): Action() {
   class Tap(val key: NavigationalWarningKey): NavigationalWarningAction() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${NavigationWarningRoute.Detail.name}?key=$encoded")
      }
   }

   class Zoom(val bounds: LatLngBounds): Action() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(Bounds.fromLatLngBounds(bounds)))
         navController.navigate(MapRoute.Map.name + "?bounds=${encoded}")
      }
   }

   class Share(val warning: NavigationalWarning): NavigationalWarningAction()
   class Location(val text: String): NavigationalWarningAction()
}
