package mil.nga.msi.ui.asam

import android.net.Uri
import androidx.compose.ui.graphics.Color
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
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route

sealed class AsamRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color(0xFF000000)
): Route {
   object Main: AsamRoute("asams", "ASAMs")
   object Detail: AsamRoute("asams/detail", "ASAM Details")
   object List: AsamRoute("asams/list", "ASAMs")
   object Sheet: AsamRoute("asams/sheet", "ASAM Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.asamGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareAsam: (String) -> Unit = {
      share(Pair("Share ASAM Information", it))
   }

   val zoomTo: (Point) -> Unit = { point ->
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
   }

   navigation(
      route = AsamRoute.Main.name,
      startDestination = AsamRoute.List.name
   ) {
      composable(AsamRoute.List.name) {
         bottomBarVisibility(true)

         AsamsScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { reference ->
               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
            },
            onAction = { action ->
               when(action) {
                  is AsamAction.Zoom -> zoomTo(action.point)
                  is AsamAction.Share -> shareAsam(action.text)
                  is AsamAction.Location -> showSnackbar("${action.text} copied to clipboard")
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
                     is AsamAction.Zoom -> zoomTo(action.point)
                     is AsamAction.Share -> shareAsam(action.text)
                     is AsamAction.Location -> showSnackbar("${action.text} copied to clipboard")
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
   }
}