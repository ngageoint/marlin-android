package mil.nga.msi.ui.dgpsstation

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.dgpsstation.list.DgpsStationsScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route

sealed class DgpsStationRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color(0xFFFFB300)
): Route {
   object Main: DgpsStationRoute("dgpsStations", "Differential GPS Stations", "DGPS")
   object Detail: DgpsStationRoute("dgpsStations/detail", "Differential GPS Station Details", "DGPS Details")
   object List: DgpsStationRoute("dgpsStations/list", "Differential GPS Stations", "DGPS Stations")
   object Sheet: DgpsStationRoute("dgpsStations/sheet", "Differential GPS Station Sheet", "DGPS Station Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.dgpsStationGraph(
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
      route = DgpsStationRoute.Main.name,
      startDestination = DgpsStationRoute.List.name
   ) {
      composable(DgpsStationRoute.List.name) {
         bottomBarVisibility(true)

         DgpsStationsScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { key ->
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${DgpsStationRoute.Detail.name}?key=$encoded")
            },
            onAction = { action ->
               when(action) {
                  is DgpsStationAction.Zoom -> zoomTo(action.point)
                  is DgpsStationAction.Share -> shareLight(action.text)
                  is DgpsStationAction.Location -> showSnackbar("${action.text} copied to clipboard")
               }
            }
         )
      }

//      composable(
//         route = "${RadioBeaconRoute.Detail.name}?key={key}",
//         arguments = listOf(navArgument("key") { type = NavType.RadioBeacon })
//      ) { backstackEntry ->
//         bottomBarVisibility(false)
//
//         backstackEntry.arguments?.getParcelable<RadioBeaconKey>("key")?.let { key ->
//            RadioBeaconDetailScreen(
//               key,
//               close = { navController.popBackStack() },
//               onAction = { action ->
//                  when(action) {
//                     is RadioBeaconAction.Zoom -> zoomTo(action.point)
//                     is RadioBeaconAction.Share -> shareLight(action.text)
//                     is RadioBeaconAction.Location -> showSnackbar("${action.text} copied to clipboard")
//                  }
//               }
//            )
//         }
//      }
//
//      bottomSheet(
//         route = "${RadioBeaconRoute.Sheet.name}?key={key}",
//         arguments = listOf(navArgument("key") { type = NavType.RadioBeacon })
//      ) { backstackEntry ->
//         backstackEntry.arguments?.getParcelable<RadioBeaconKey>("key")?.let { key ->
//            RadioBeaconSheetScreen(key, onDetails = {
//               val encoded = Uri.encode(Json.encodeToString(key))
//               navController.navigate( "${RadioBeaconRoute.Detail.name}?key=$encoded")
//            })
//         }
//      }
   }
}