package mil.nga.msi.ui.port

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
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.detail.PortDetailScreen
import mil.nga.msi.ui.port.list.PortsScreen
import mil.nga.msi.ui.port.sheet.PortSheetScreen

sealed class PortRoute(
   override val name: String,
   override val title: String,
   override val color: Color = Color(0xFF5856d6)
): Route {
   object Main: PortRoute("ports", "Ports")
   object Detail: PortRoute("ports/detail", "Port Details")
   object List: PortRoute("ports/list", "Ports")
   object Sheet: PortRoute("ports/sheet", "Port Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.portGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareAsam: (String) -> Unit = {
      share(Pair("Share Port Information", it))
   }

   val zoomTo: (Point) -> Unit = { point ->
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
   }

   navigation(
      route = PortRoute.Main.name,
      startDestination = PortRoute.List.name
   ) {
      composable(PortRoute.List.name) {
         bottomBarVisibility(true)

         PortsScreen(
            openDrawer = { openNavigationDrawer() },
            onTap = { portNumber ->
               navController.navigate("${PortRoute.Detail.name}?portNumber=$portNumber")
            },
            onAction = { action ->
               when(action) {
                  is PortAction.Zoom -> zoomTo(action.point)
                  is PortAction.Share -> shareAsam(action.text)
                  is PortAction.Location -> showSnackbar("${action.text} copied to clipboard")
               }
            }
         )
      }
      composable("${PortRoute.Detail.name}?portNumber={portNumber}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("portNumber")?.toIntOrNull()?.let { portNumber ->
            PortDetailScreen(
               portNumber,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is PortAction.Zoom -> zoomTo(action.point)
                     is PortAction.Share -> shareAsam(action.text)
                     is PortAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  }
               }
            )
         }
      }
      bottomSheet("${PortRoute.Sheet.name}?portNumber={portNumber}") { backstackEntry ->
         backstackEntry.arguments?.getString("portNumber")?.let { portNumber ->
            PortSheetScreen(portNumber, onDetails = {
               navController.navigate("${PortRoute.Detail.name}?portNumber=$portNumber")
            })
         }
      }
   }
}