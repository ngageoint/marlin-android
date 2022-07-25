package mil.nga.msi.ui.light

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.light.list.LightsScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route

sealed class LightRoute(
   override val name: String,
   override val title: String,
): Route {
   object Main: LightRoute("lights", "Lights")
//   object Detail: AsamRoute("asams/detail", "ASAM Details")
   object List: LightRoute("lights/list", "Lights")
//   object Sheet: AsamRoute("asams/sheet", "ASAM Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.lightGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareLight: (String) -> Unit = {
      share(Pair("Share Light Information", it))
   }

   val zoomTo: (Point) -> Unit = { point ->
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
   }

   navigation(
      route = LightRoute.Main.name,
      startDestination = LightRoute.List.name
   ) {
      composable(LightRoute.List.name) {
         bottomBarVisibility(true)

         LightsScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { reference ->
//               navController.navigate("${LightRoute.Detail.name}?reference=$reference")
            },
            onAction = { action ->
               when(action) {
                  is LightAction.Zoom -> zoomTo(action.point)
                  is LightAction.Share -> shareLight(action.text)
                  is LightAction.Location -> showSnackbar("${action.text} copied to clipboard")
               }
            }
         )
      }
//      composable("${AsamRoute.Detail.name}?reference={reference}") { backstackEntry ->
//         bottomBarVisibility(false)
//
//         backstackEntry.arguments?.getString("reference")?.let { reference ->
//            AsamDetailScreen(
//               reference,
//               close = { navController.popBackStack() },
//               onAction = { action ->
//                  when(action) {
//                     is AsamAction.Zoom -> zoomTo(action.point)
//                     is AsamAction.Share -> shareAsam(action.text)
//                     is AsamAction.Location -> showSnackbar("${action.text} copied to clipboard")
//                  }
//               }
//            )
//         }
//      }
//      bottomSheet("${AsamRoute.Sheet.name}?reference={reference}") { backstackEntry ->
//         backstackEntry.arguments?.getString("reference")?.let { reference ->
//            AsamSheetScreen(reference, onDetails = {
//               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
//            })
//         }
//      }
   }
}