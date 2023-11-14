package mil.nga.msi.ui.port

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.ui.action.PortAction
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.detail.PortDetailScreen
import mil.nga.msi.ui.port.list.PortsScreen
import mil.nga.msi.ui.sort.SortScreen

sealed class PortRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Main: PortRoute("ports", "World Ports", "Ports")
   data object Detail: PortRoute("ports/detail", "World Port Details", "Port Details")
   data object List: PortRoute("ports/list", "World Ports", "Ports")
   data object Filter: PortRoute("ports/filter", "World Port Filter", "Port Filters")
   data object Sort: PortRoute("ports/sort", "World Port Sort", "Port Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.portGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val sharePort: (Port) -> Unit = { port ->
      share(Pair("Share Port Information", port.toString()))
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
               appState.navController.navigate(PortRoute.Filter.name)
            },
            openSort = {
               appState.navController.navigate(PortRoute.Sort.name)
            },
            onAction = { action ->
               when(action) {
                  is PortAction.Share -> sharePort(action.port)
                  is PortAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(appState.navController)
               }
            }
         )
      }
      composable("${PortRoute.Detail.name}?portNumber={portNumber}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("portNumber")?.toIntOrNull()?.let { portNumber ->
            PortDetailScreen(
               portNumber,
               close = { appState.navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is PortAction.Share -> sharePort(action.port)
                     is PortAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(appState.navController)
                  }
               }
            )
         }
      }

      bottomSheet(PortRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.PORT,
            close = { appState.navController.popBackStack() }
         )
      }

      bottomSheet(PortRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.PORT,
            close = { appState.navController.popBackStack() }
         )
      }
   }
}