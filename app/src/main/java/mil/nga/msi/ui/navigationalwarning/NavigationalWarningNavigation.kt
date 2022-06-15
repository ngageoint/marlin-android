package mil.nga.msi.ui.navigationalwarning

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.Route
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
            onTap = { number ->
               navController.navigate( "${NavigationWarningRoute.Detail.name}?number=$number")
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
}