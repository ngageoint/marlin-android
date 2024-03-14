package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.buildZoomNavOptions
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.export.ExportRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute

sealed class NavigationalWarningAction : Action() {
   class Export(val dataSource: DataSource, val navigationArea: NavigationArea): Action() {
      override fun navigate(navController: NavController) {
         navController.navigate("${ExportRoute.Export.name}?dataSource=${dataSource}&navigationAreaCode=${navigationArea.code}")
      }
   }

   class Tap(val key: NavigationalWarningKey): NavigationalWarningAction() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${NavigationWarningRoute.Detail.name}?key=$encoded")
      }
   }

   class Zoom(val bounds: LatLngBounds): NavigationalWarningAction() {
      override fun navigate(navController: NavController) {
         val encoded = Uri.encode(Json.encodeToString(Bounds.fromLatLngBounds(bounds)))
         navController.navigate(MapRoute.Map.name + "?bounds=${encoded}", buildZoomNavOptions(navController))
      }
   }

   class Share(val warning: NavigationalWarning): NavigationalWarningAction()
   class Location(val text: String): NavigationalWarningAction()
}
