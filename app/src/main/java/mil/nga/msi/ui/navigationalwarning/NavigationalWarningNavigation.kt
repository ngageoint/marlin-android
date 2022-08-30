package mil.nga.msi.ui.navigationalwarning

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.navigation.NavigationalWarningKey
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningDetailScreen
import mil.nga.msi.ui.navigationalwarning.list.NavigationalWarningsScreen

sealed class NavigationWarningRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color(0xFFD32F2F)
): Route {
   object Main: NavigationWarningRoute("navigational_warnings", "Navigational Warnings", "Warnings")
   object Group: NavigationWarningRoute("navigational_warnings/group", "Navigational Warnings", "Navigational Warnings")
   object List: NavigationWarningRoute("navigational_warnings/list", "Navigational Warnings", "Navigational Warnings")
   object Detail: NavigationWarningRoute("navigational_warnings/detail", "Navigational Warning Details", "Navigational Warning Details")
}

fun NavGraphBuilder.navigationalWarningGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit
) {
   val shareNavigationalWarning: (String) -> Unit = {
      share(Pair("Share Navigational Warning Information", it))
   }

   navigation(
      route = NavigationWarningRoute.Main.name,
      startDestination = NavigationWarningRoute.Group.name,
   ) {
      composable(NavigationWarningRoute.Group.name) {
         bottomBarVisibility(true)

         NavigationalWarningGroupScreen(
            openDrawer = { openNavigationDrawer() },
            onGroupTap = { navigationArea ->
               navController.navigate( "${NavigationWarningRoute.List.name}?navigationArea=${navigationArea.code}")
            }
         )
      }
      composable(
         route = "${NavigationWarningRoute.List.name}?navigationArea={navigationAreaCode}",
         arguments = listOf(navArgument("navigationAreaCode") { type = NavType.StringType })
      ) { backstackEntry ->
         bottomBarVisibility(true)

         backstackEntry.arguments?.getString("navigationAreaCode")?.let { navigationAreaCode ->
            val navigationArea = NavigationArea.fromCode(navigationAreaCode)!!
            NavigationalWarningsScreen(
               navigationArea,
               close = { navController.popBackStack() },
               onTap = { key ->
                  val encoded = Uri.encode(Json.encodeToString(key))
                  navController.navigate( "${NavigationWarningRoute.Detail.name}?key=$encoded")
               },
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.text)
                     }
                  }
               }
            )
         }
      }
      composable(
         route = "${NavigationWarningRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.NavigationalWarningKey })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getParcelable<NavigationalWarningKey>("key")?.let { key ->
            NavigationalWarningDetailScreen(
               key = key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.text)
                     }
                  }
               }
            )
         }
      }
   }
}