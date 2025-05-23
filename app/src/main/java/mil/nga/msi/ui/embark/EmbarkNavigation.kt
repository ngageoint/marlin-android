package mil.nga.msi.ui.embark

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.theme.MsiEmbarkTheme

sealed class EmbarkRoute(
   override val name: String,
   override val title: String = "",
   override val shortTitle: String = ""
): Route {
   data object Welcome: EmbarkRoute("embark/welcome")
   data object Disclaimer: EmbarkRoute("embark/disclaimer")
   data object Location: EmbarkRoute("embark/Location")
   data object Notification: EmbarkRoute("embark/notification")
   data object Tabs: EmbarkRoute("embark/tabs")
   data object Map: EmbarkRoute("embark/map")
}

fun NavGraphBuilder.embarkGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit
) {
   composable(EmbarkRoute.Welcome.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         WelcomeScreen(
            done = {
               appState.navController.navigate(EmbarkRoute.Disclaimer.name)
            }
         )
      }
   }

   composable(EmbarkRoute.Disclaimer.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         DisclaimerScreen(
            done = {
               appState.navController.navigate(EmbarkRoute.Location.name)
            }
         )
      }
   }

   composable(EmbarkRoute.Location.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         LocationScreen(
            done = {
               appState.navController.navigate(EmbarkRoute.Notification.name)
            }
         )
      }
   }

   composable(EmbarkRoute.Notification.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         NotificationScreen(
            done = {
               appState.navController.navigate(EmbarkRoute.Tabs.name)
            }
         )
      }
   }

   composable(EmbarkRoute.Tabs.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         TabsScreen(
            done = {
               appState.navController.navigate(EmbarkRoute.Map.name)
            }
         )
      }
   }

   composable(EmbarkRoute.Map.name) {
      bottomBarVisibility(false)

      MsiEmbarkTheme {
         MapScreen(
            done = {
               appState.navController.navigate(MapRoute.Map.name) {
                  popUpTo(EmbarkRoute.Welcome.name) {
                     inclusive = true
                  }
               }
            }
         )
      }
   }
}