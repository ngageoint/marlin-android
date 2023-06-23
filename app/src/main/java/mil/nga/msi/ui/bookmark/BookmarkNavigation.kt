package mil.nga.msi.ui.bookmark

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.Route

sealed class BookmarkRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color.Transparent
): Route {
   object Main: BookmarkRoute("bookmarks/main", "Bookmarks", "Bookmarks")
   object List: BookmarkRoute("bookmarks/list", "Bookmarks", "Bookmarks")
}

fun NavGraphBuilder.bookmarksGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit
) {
   navigation(
      route = BookmarkRoute.Main.name,
      startDestination = BookmarkRoute.List.name
   ) {
      composable(
         route = BookmarkRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${BookmarkRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         BookmarksScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = {},
            onAction = {}
         )
      }
   }
}