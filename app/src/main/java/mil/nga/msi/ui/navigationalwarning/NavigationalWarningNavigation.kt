package mil.nga.msi.ui.navigationalwarning

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.map.MapPosition
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigation.NavTypeBounds
import mil.nga.msi.ui.navigation.NavTypeNavigationalWarningKey
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningDetailScreen
import mil.nga.msi.ui.navigationalwarning.list.NavigationalWarningsScreen
import mil.nga.msi.ui.navigationalwarning.sheet.NavigationalWarningSheetScreen

sealed class NavigationWarningRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.NAVIGATION_WARNING.color
): Route {
   object Main: NavigationWarningRoute("navigational_warnings", "Navigational Warnings", "Warnings")
   object Group: NavigationWarningRoute("navigational_warnings/group", "Navigational Warnings", "Navigational Warnings")
   object List: NavigationWarningRoute("navigational_warnings/list", "Navigational Warnings", "Navigational Warnings")
   object Detail: NavigationWarningRoute("navigational_warnings/detail", "Navigational Warning Details", "Navigational Warning Details")
   object Sheet: NavigationWarningRoute("navigational_warnings/sheet", "Navigational Warning Sheet", "Navigational Warning Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.navigationalWarningGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit
) {
   val shareNavigationalWarning: (NavigationalWarning) -> Unit = { warning ->
      share(Pair("Share Navigational Warning Information", warning.toString()))
   }

   navigation(
      route = NavigationWarningRoute.Main.name,
      startDestination = "${NavigationWarningRoute.Group.name}?bounds={bounds}",
   ) {
      composable(
         route = "${NavigationWarningRoute.Group.name}?bounds={bounds}",
         arguments = listOf(
            navArgument("bounds") {
               nullable = true
               defaultValue = null
               type = NavType.NavTypeBounds
            }
         ),
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${NavigationWarningRoute.Group.name}" })
      ) { backstackEntry ->
         bottomBarVisibility(true)

         val mapPosition = backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "bounds", Bounds::class.java)?.asLatLngBounds()?.let {
               MapPosition(bounds = it)
            }
         }

         NavigationalWarningGroupScreen(
            position = mapPosition,
            openDrawer = { openNavigationDrawer() },
            onExport = {
               Action.Export(ExportDataSource.NavigationalWarning()).navigate(navController)
            },
            onGroupTap = { navigationArea ->
               navController.navigate( "${NavigationWarningRoute.List.name}?navigationArea=${navigationArea.code}")
            },
            onNavigationWarningsTap = { keys ->
               if (keys.size == 1) {
                  val encoded = Uri.encode(Json.encodeToString(keys.first()))
                  navController.navigate(NavigationWarningRoute.Sheet.name + "?key=${encoded}")
               } else {
                  val annotations = keys.map { key ->
                     MapAnnotation(MapAnnotation.Key(key.id(), MapAnnotation.Type.NAVIGATIONAL_WARNING), 0.0, 0.0)
                  }
                  val encoded = Uri.encode(Json.encodeToString(annotations))
                  navController.navigate(MapRoute.PagerSheet.name + "?annotations=${encoded}")
               }
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
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.warning)
                     }
                     else -> action.navigate(navController)
                  }
               }
            )
         }
      }

      composable(
         route = "${NavigationWarningRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.NavTypeNavigationalWarningKey })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", NavigationalWarningKey::class.java)
         }?.let { key ->
            NavigationalWarningDetailScreen(
               key = key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.warning)
                     }
                     else -> action.navigate(navController)
                  }
               }
            )
         }
      }

      bottomSheet(
         route = "${NavigationWarningRoute.Sheet.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.NavTypeNavigationalWarningKey })
      ) { backstackEntry ->
         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", NavigationalWarningKey::class.java)
         }?.let { key ->
            NavigationalWarningSheetScreen(
               key = key,
               onDetails = {
                  val encoded = Uri.encode(Json.encodeToString(key))
                  navController.navigate( "${NavigationWarningRoute.Detail.name}?key=$encoded")
               }
            )
         }
      }
   }
}