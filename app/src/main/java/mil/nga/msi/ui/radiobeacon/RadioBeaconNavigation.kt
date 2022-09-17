package mil.nga.msi.ui.radiobeacon

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.RadioBeacon
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.radiobeacon.detail.RadioBeaconDetailScreen
import mil.nga.msi.ui.radiobeacon.list.RadioBeaconsScreen
import mil.nga.msi.ui.radiobeacon.sheet.RadioBeaconSheetScreen

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
   }
}