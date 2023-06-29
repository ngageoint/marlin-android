package mil.nga.msi.ui.action

import android.net.Uri
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightRoute

sealed class LightAction(): Action() {
   class Tap(val light: Light): LightAction() {
      override fun navigate(navController: NavController) {
         val key = LightKey.fromLight(light)
         val encoded = Uri.encode(Json.encodeToString(key))
         navController.navigate( "${LightRoute.Detail.name}?key=$encoded")
      }
   }

   class Location(val text: String): LightAction()
   class Share(val light: Light) : LightAction()
}