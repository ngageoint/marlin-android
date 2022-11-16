package mil.nga.msi.ui.embark

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Route

sealed class EmbarkRoute(
   override val name: String,
   override val title: String = "",
   override val shortTitle: String = "",
   override val color: Color = Color.Transparent
): Route {
   object Welcome: EmbarkRoute("embark/welcome")
   object Disclaimer: EmbarkRoute("embark/disclaimer")
   object Location: EmbarkRoute("embark/Location")
   object Notification: EmbarkRoute("embark/notification")
   object Tabs: EmbarkRoute("embark/tabs")
   object Map: EmbarkRoute("embark/map")
}

// TODO need something that handles how to navigate this graph

fun NavGraphBuilder.embarkGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit
) {
   composable(EmbarkRoute.Welcome.name) {
      bottomBarVisibility(false)

      WelcomeScreen(
         done = {
            navController.navigate(EmbarkRoute.Disclaimer.name)
         }
      )
   }

   composable(EmbarkRoute.Disclaimer.name) {
      bottomBarVisibility(false)

      DisclaimerScreen(
         done = {
            navController.navigate(EmbarkRoute.Location.name)
         }
      )
   }

   composable(EmbarkRoute.Location.name) {
      bottomBarVisibility(false)
      
      LocationScreen(
         done = {
            navController.navigate(EmbarkRoute.Notification.name)
         }
      )
   }

   composable(EmbarkRoute.Notification.name) {
      bottomBarVisibility(false)

      NotificationScreen(
         done = {
            navController.navigate(EmbarkRoute.Tabs.name)
         }
      )
   }

   composable(EmbarkRoute.Tabs.name) {
      bottomBarVisibility(false)

      TabsScreen(
         done = {
            navController.navigate(EmbarkRoute.Map.name)
         }
      )
   }

   composable(EmbarkRoute.Map.name) {
      bottomBarVisibility(false)

      MapScreen(
         done = {
            navController.navigate(MapRoute.Map.name) {
               popUpTo(EmbarkRoute.Welcome.name) {
                  inclusive = true
               }
            }
         }
      )
   }
}