package mil.nga.msi.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.Route

sealed class SettingsRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color(0xFF000000)
): Route {
   object Main: SettingsRoute("settings", "Settings")
   object List: SettingsRoute("settings/list", "Settings")
   object Disclaimer: SettingsRoute("settings/disclaimer", "Disclaimer")
   object About: SettingsRoute("settings/about", "About")
}

fun NavGraphBuilder.settingsGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit
) {
   navigation(
      route = SettingsRoute.Main.name,
      startDestination = SettingsRoute.List.name
   ) {
      composable(SettingsRoute.List.name) {
         bottomBarVisibility(true)

         SettingsScreen(
            close = { navController.popBackStack() },
            onDisclaimer = {
               navController.navigate(SettingsRoute.Disclaimer.name)
            },
            onAbout = {
               navController.navigate(SettingsRoute.About.name)
            }
         )
      }

      composable(SettingsRoute.Disclaimer.name) {
         bottomBarVisibility(true)

         DisclaimerScreen(
            close = { navController.popBackStack() }
         )
      }

      composable(SettingsRoute.About.name) {
         bottomBarVisibility(true)

         AboutScreen(
            close = { navController.popBackStack() }
         )
      }
   }
}