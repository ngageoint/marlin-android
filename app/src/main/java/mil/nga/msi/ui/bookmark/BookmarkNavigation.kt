package mil.nga.msi.ui.bookmark

import androidx.compose.ui.graphics.Color
import androidx.core.os.BundleCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.navigation.NavTypeBookmark
import mil.nga.msi.ui.navigation.Route

sealed class BookmarkRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color.Transparent
): Route {
   object Main: BookmarkRoute("bookmarks/main", "Bookmarks", "Bookmarks")
   object List: BookmarkRoute("bookmarks/list", "Bookmarks", "Bookmarks")
   object Notes: BookmarkRoute("bookmarks/notes", "Bookmark Notes", "Notes")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
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

      bottomSheet(
         route = "${BookmarkRoute.Notes.name}?bookmark={bookmark}",
         arguments = listOf(navArgument("bookmark") { type = NavType.NavTypeBookmark })
      ) { backstackEntry ->
         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "bookmark", BookmarkKey::class.java)
         }?.let { bookmark ->
            BookmarkNotesScreen(
               bookmark = bookmark,
               onDone = { navController.popBackStack() }
            )
         }
      }
   }
}