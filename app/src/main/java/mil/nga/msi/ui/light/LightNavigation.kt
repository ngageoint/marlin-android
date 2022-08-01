package mil.nga.msi.ui.light

import android.net.Uri
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.light.detail.LightDetailScreen
import mil.nga.msi.ui.light.list.LightsScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.LightKey
import mil.nga.msi.ui.navigation.NavigationalWarningKey
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute

sealed class LightRoute(
   override val name: String,
   override val title: String,
): Route {
   object Main: LightRoute("lights", "Lights")
   object Detail: LightRoute("lights/detail", "Light Details")
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
            onTap = { light ->
               val encoded = Uri.encode(Json.encodeToString(LightKey.fromLight(light)))
               navController.navigate( "${LightRoute.Detail.name}?key=$encoded")
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
      composable(
         route = "${LightRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.LightKey })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getParcelable<LightKey>("key")?.let { key ->
            LightDetailScreen(
               key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is LightAction.Zoom -> zoomTo(action.point)
                     is LightAction.Share -> shareLight(action.text)
                     is LightAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  }
               }
            )
         }
      }
//      bottomSheet("${AsamRoute.Sheet.name}?reference={reference}") { backstackEntry ->
//         backstackEntry.arguments?.getString("reference")?.let { reference ->
//            AsamSheetScreen(reference, onDetails = {
//               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
//            })
//         }
//      }
   }
}