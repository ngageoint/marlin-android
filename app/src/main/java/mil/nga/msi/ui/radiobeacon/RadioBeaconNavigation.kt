package mil.nga.msi.ui.radiobeacon

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.RadioBeacon
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.radiobeacon.detail.RadioBeaconDetailScreen
import mil.nga.msi.ui.radiobeacon.list.RadioBeaconsScreen
import mil.nga.msi.ui.radiobeacon.sheet.RadioBeaconSheetScreen
import mil.nga.msi.ui.sort.SortScreen

sealed class RadioBeaconRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color(0xFF007BFF)
): Route {
   object Main: RadioBeaconRoute("radioBeacons", "Radio Beacons", "Beacons")
   object Detail: RadioBeaconRoute("radioBeacons/detail", "Radio Beacon Details", "Beacon Details")
   object List: RadioBeaconRoute("radioBeacons/list", "Radio Beacons", "Beacons")
   object Sheet: RadioBeaconRoute("radioBeacons/sheet", "Radio Beacon Sheet", "Beacon Sheet")
   object Filter: RadioBeaconRoute("radioBeacons/filter", "Radio Beacon Filter", "Radio Beacon Filter")
   object Sort: RadioBeaconRoute("radioBeacons/sort", "Radio Beacon Sort", "Radio Beacon Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.radioBeaconGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareLight: (String) -> Unit = {
      share(Pair("Share Radio Beacon Information", it))
   }

   val zoomTo: (Point) -> Unit = { point ->
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
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
            onTap = { key ->
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${RadioBeaconRoute.Detail.name}?key=$encoded")
            },
            onAction = { action ->
               when(action) {
                  is RadioBeaconAction.Zoom -> zoomTo(action.point)
                  is RadioBeaconAction.Share -> shareLight(action.text)
                  is RadioBeaconAction.Location -> showSnackbar("${action.text} copied to clipboard")
               }
            }
         )
      }

      composable(
         route = "${RadioBeaconRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.RadioBeacon })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getParcelable<RadioBeaconKey>("key")?.let { key ->
            RadioBeaconDetailScreen(
               key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is RadioBeaconAction.Zoom -> zoomTo(action.point)
                     is RadioBeaconAction.Share -> shareLight(action.text)
                     is RadioBeaconAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  }
               }
            )
         }
      }

      bottomSheet(
         route = "${RadioBeaconRoute.Sheet.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.RadioBeacon })
      ) { backstackEntry ->
         backstackEntry.arguments?.getParcelable<RadioBeaconKey>("key")?.let { key ->
            RadioBeaconSheetScreen(key, onDetails = {
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${RadioBeaconRoute.Detail.name}?key=$encoded")
            })
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