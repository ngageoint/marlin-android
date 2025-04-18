package mil.nga.msi.ui.dgpsstation

import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.detail.DgpsStationDetailScreen
import mil.nga.msi.ui.dgpsstation.list.DgpsStationsScreen
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.navigation.DgpsStation
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class DgpsStationRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Main: DgpsStationRoute("dgpsStations", "Differential GPS Stations", "DGPS")
   data object Detail: DgpsStationRoute("dgpsStations/detail", "Differential GPS Station Details", "DGPS Details")
   data object List: DgpsStationRoute("dgpsStations/list", "Differential GPS Stations", "DGPS Stations")
   data object Filter: DgpsStationRoute("dgpsStations/filter", "Differential GPS Station Filter", "DGPS Station Filter")
   data  object Sort: DgpsStationRoute("dgpsStations/sort", "Differential GPS Station Sort", "DGPS Station Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.dgpsStationGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareDgps: (DgpsStation) -> Unit = {
      share(Pair("Share DGPS Information", it.toString()))
   }

   navigation(
      route = DgpsStationRoute.Main.name,
      startDestination = DgpsStationRoute.List.name
   ) {
      composable(
         route = DgpsStationRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${DgpsStationRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         DgpsStationsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = { appState.navController.navigate(DgpsStationRoute.Filter.name) },
            openSort = { appState.navController.navigate(DgpsStationRoute.Sort.name) },
            onAction = { action ->
               when(action) {
                  is DgpsStationAction.Share -> shareDgps(action.dgpsStation)
                  is DgpsStationAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(appState.navController)
               }
            }
         )
      }

      composable(
         route = "${DgpsStationRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.DgpsStation })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", DgpsStationKey::class.java)
         }?.let { key ->
            DgpsStationDetailScreen(
               key,
               close = { appState.navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is DgpsStationAction.Share -> shareDgps(action.dgpsStation)
                     is DgpsStationAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(appState.navController)
                  }
               }
            )
         }
      }

      bottomSheet(DgpsStationRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.DGPS_STATION,
            close = { appState.navController.popBackStack() }
         )
      }
      bottomSheet(DgpsStationRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.DGPS_STATION,
            close = { appState.navController.popBackStack() }
         )
      }
   }
}