package mil.nga.msi.ui.navigationalwarning

import android.net.Uri
import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.map.MapPosition
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.navigation.NavTypeBounds
import mil.nga.msi.ui.navigation.NavTypeNavigationalWarningKey
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.detail.NavigationalWarningDetailScreen
import mil.nga.msi.ui.navigationalwarning.list.NavigationalWarningsScreen

sealed class NavigationWarningRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Main: NavigationWarningRoute("navigational_warnings", "Navigational Warnings", "Warnings")
   data object Group: NavigationWarningRoute("navigational_warnings/group", "Navigational Warnings", "Navigational Warnings")
   data object List: NavigationWarningRoute("navigational_warnings/list", "Navigational Warnings", "Navigational Warnings")
   data object Detail: NavigationWarningRoute("navigational_warnings/detail", "Navigational Warning Details", "Navigational Warning Details")
}

fun NavGraphBuilder.navigationalWarningGraph(
   appState: MarlinAppState,
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
               Action.Export(listOf(ExportDataSource.NavigationalWarning())).navigate(appState.navController)
            },
            onGroupTap = { navigationArea ->
               appState.navController.navigate( "${NavigationWarningRoute.List.name}?navigationArea=${navigationArea.code}")
            },
            onNavigationWarningsTap = { latLng, bounds ->
               val encodedPoint = Uri.encode(Json.encodeToString(NavPoint(latLng.latitude, latLng.longitude)))
               val encodedBounds = Uri.encode(Json.encodeToString(Bounds.fromLatLngBounds(bounds)))
               appState.navController.navigate(MapRoute.PagerSheet.name + "?point=${encodedPoint}&bounds=${encodedBounds}")
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
               close = { appState.navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.warning)
                     }
                     else -> action.navigate(appState.navController)
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
               close = { appState.navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is NavigationalWarningAction.Share -> {
                        shareNavigationalWarning(action.warning)
                     }
                     else -> action.navigate(appState.navController)
                  }
               }
            )
         }
      }
   }
}