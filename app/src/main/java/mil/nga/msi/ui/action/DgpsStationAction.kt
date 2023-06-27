package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute

sealed class DgpsStationAction(): Action() {
   class Tap(val dgpsStation: DgpsStation): DgpsStationAction() {
      override fun navigate(navController: NavController) {
         val key = DgpsStationKey.fromDgpsStation(dgpsStation)
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${DgpsStationRoute.Detail.name}?key=$encoded")
      }
   }

   class Share(val dgpsStation: DgpsStation): DgpsStationAction()
   class Location(val text: String): DgpsStationAction()
}