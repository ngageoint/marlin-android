package mil.nga.msi.ui.navigationalwarning

import android.net.Uri
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.navigation.NavigationalWarningKey
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningDetailScreen
import mil.nga.msi.ui.navigationalwarning.list.NavigationalWarningsScreen

sealed class NavigationWarningRoute(
   override val name: String,
   override val title: String,
): Route {
   object Main: NavigationWarningRoute("navigational_warnings", "Navigational Warnings")
   object Detail: NavigationWarningRoute("navigational_warnings/detail", "Navigational Warning Details")
   object List: NavigationWarningRoute("navigational_warnings/list", "Navigational Warnings")
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
      startDestination = NavigationWarningRoute.List.name,
   ) {
      composable(NavigationWarningRoute.List.name) {
         bottomBarVisibility(true)

         NavigationalWarningsScreen(
            openDrawer = { openNavigationDrawer() },
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