package mil.nga.msi.ui.asam

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.bookmark.BookmarkRoute
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class AsamRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.ASAM.color
): Route {
   object Main: AsamRoute("asams", "Anti-Shipping Activity Messages", "ASAMs")
   object Detail: AsamRoute("asams/detail", "Anti-Shipping Activity Message Details", "ASAM Details")
   object List: AsamRoute("asams/list", "Anti-Shipping Activity Messages", "ASAMs")
   object Sheet: AsamRoute("asams/sheet", "Anti-Shipping Activity Message Sheet", "ASAM Sheet")
   object Filter: AsamRoute("asams/filter", "Anti-Shipping Activity Message Filter", "ASAM Filters")
   object Sort: AsamRoute("asams/sort", "Anti-Shipping Activity Message Sort", "ASAM Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.asamGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareAsam: (Asam) -> Unit = { asam ->
      share(Pair("Share ASAM Information", asam.toString()))
   }

   val zoomTo: (Asam) -> Unit = { asam ->
      val point = NavPoint(asam.latitude, asam.longitude)
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
   }

   navigation(
      route = AsamRoute.Main.name,
      startDestination = AsamRoute.List.name
   ) {
      composable(
         route = AsamRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${AsamRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         AsamsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = {
               navController.navigate(AsamRoute.Filter.name)
            },
            openSort = {
               navController.navigate(AsamRoute.Sort.name)
            },
            onTap = { reference ->
               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
            },
            onAction = { action ->
               when(action) {
                  is AsamAction.Zoom -> zoomTo(action.asam)
                  is AsamAction.Share -> shareAsam(action.asam)
                  is AsamAction.Location -> {
                     showSnackbar("${action.text} copied to clipboard")
                  }
                  is AsamAction.Bookmark -> {
                     val encoded = Uri.encode(Json.encodeToString(action.bookmark))
                     navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
                  }
               }
            }
         )
      }
      composable("${AsamRoute.Detail.name}?reference={reference}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("reference")?.let { reference ->
            AsamDetailScreen(
               reference,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is AsamAction.Zoom -> zoomTo(action.asam)
                     is AsamAction.Share -> shareAsam(action.asam)
                     is AsamAction.Location -> {
                        showSnackbar("${action.text} copied to clipboard")
                     }
                     is AsamAction.Bookmark -> {
                        val encoded = Uri.encode(Json.encodeToString(action.bookmark))
                        navController.navigate( "${BookmarkRoute.Notes.name}?bookmark=$encoded")
                     }
                  }
               }
            )
         }
      }
      bottomSheet("${AsamRoute.Sheet.name}?reference={reference}") { backstackEntry ->
         backstackEntry.arguments?.getString("reference")?.let { reference ->
            AsamSheetScreen(reference, onDetails = {
               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
            })
         }
      }
      bottomSheet(AsamRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.ASAM,
            close = {
               navController.popBackStack()
            }
         )
      }
      bottomSheet(AsamRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.ASAM,
            close = {
               navController.popBackStack()
            }
         )
      }
   }
}