package mil.nga.msi.ui.port

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.detail.PortDetailScreen
import mil.nga.msi.ui.port.list.PortsScreen
import mil.nga.msi.ui.port.sheet.PortSheetScreen
import mil.nga.msi.ui.sort.SortScreen

sealed class PortRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.PORT.color
): Route {
   object Main: PortRoute("ports", "World Ports", "Ports")
   object Detail: PortRoute("ports/detail", "World Port Details", "Port Details")
   object List: PortRoute("ports/list", "World Ports", "Ports")
   object Sheet: PortRoute("ports/sheet", "World Port Sheet", "Port Sheet")
   object Filter: PortRoute("ports/filter", "World Port Filter", "Port Filters")
   object Sort: PortRoute("ports/sort", "World Port Sort", "Port Sort")
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
      composable(
         route = PortRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${PortRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         PortsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = {
               navController.navigate(PortRoute.Filter.name)
            },
            openSort = {
               navController.navigate(PortRoute.Sort.name)
            },
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
      bottomSheet(PortRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.PORT,
            close = {
               navController.popBackStack()
            }
         )
      }
      bottomSheet(PortRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.PORT,
            close = {
               navController.popBackStack()
            }
         )
      }
   }
}