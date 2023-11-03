package mil.nga.msi.ui.radiobeacon

import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.action.RadioBeaconAction
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.navigation.RadioBeacon
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.radiobeacon.detail.RadioBeaconDetailScreen
import mil.nga.msi.ui.radiobeacon.list.RadioBeaconsScreen
import mil.nga.msi.ui.sort.SortScreen

sealed class RadioBeaconRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Main: RadioBeaconRoute("radioBeacons", "Radio Beacons", "Beacons")
   data object Detail: RadioBeaconRoute("radioBeacons/detail", "Radio Beacon Details", "Beacon Details")
   data object List: RadioBeaconRoute("radioBeacons/list", "Radio Beacons", "Beacons")
   data object Filter: RadioBeaconRoute("radioBeacons/filter", "Radio Beacon Filter", "Radio Beacon Filter")
   data object Sort: RadioBeaconRoute("radioBeacons/sort", "Radio Beacon Sort", "Radio Beacon Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.radioBeaconGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareBeacon: (RadioBeacon) -> Unit = { beacon ->
      share(Pair("Share Radio Beacon Information", beacon.toString()))
   }

   navigation(
      route = RadioBeaconRoute.Main.name,
      startDestination = RadioBeaconRoute.List.name
   ) {
      composable(
         route = RadioBeaconRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${RadioBeaconRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         RadioBeaconsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = {
               navController.navigate(RadioBeaconRoute.Filter.name)
            },
            openSort = {
               navController.navigate(RadioBeaconRoute.Sort.name)
            },
            onAction = { action ->
               when (action) {
                  is RadioBeaconAction.Share -> shareBeacon(action.radioBeacon)
                  is RadioBeaconAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(navController)
               }
            }
         )
      }

      composable(
         route = "${RadioBeaconRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.RadioBeacon })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", RadioBeaconKey::class.java)
         }?.let { key ->
            RadioBeaconDetailScreen(
               key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when (action) {
                     is RadioBeaconAction.Share -> shareBeacon(action.radioBeacon)
                     is RadioBeaconAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(navController)
                  }
               }
            )
         }
      }

      bottomSheet(RadioBeaconRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.RADIO_BEACON,
            close = {
               navController.popBackStack()
            }
         )
      }

      bottomSheet(RadioBeaconRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.RADIO_BEACON,
            close = {
               navController.popBackStack()
            }
         )
      }
   }
}