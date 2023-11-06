package mil.nga.msi.ui.modu

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.modu.detail.ModuDetailScreen
import mil.nga.msi.ui.modu.list.ModusScreen
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class ModuRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Main: ModuRoute("modus", "Mobile Offshore Drilling Units", "MODUs")
   data object Detail: ModuRoute("modus/detail", "Mobile Offshore Drilling Unit Details", "MODU Details")
   data object List: ModuRoute("modus/list", "Mobile Offshore Drilling Units", "MODUs")
   data object Filter: ModuRoute("modus/filter", "Mobile Offshore Drilling Units Filters", "MODU Filters")
   data object Sort: ModuRoute("modus/sort", "Mobile Offshore Drilling Units Sort", "MODU Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.moduGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareModu: (Modu) -> Unit = { modu ->
      share(Pair("Share MODU Information", modu.toString()))
   }

   navigation(
      route = ModuRoute.Main.name,
      startDestination = ModuRoute.List.name,
   ) {
      composable(
         route = ModuRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${ModuRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         ModusScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = { appState.navController.navigate(ModuRoute.Filter.name) },
            openSort = { appState.navController.navigate(ModuRoute.Sort.name) },
            onAction = { action ->
               when(action) {
                  is ModuAction.Share -> shareModu(action.modu)
                  is ModuAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(appState.navController)
               }
            }
         )
      }

      composable("${ModuRoute.Detail.name}?name={name}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("name")?.let { name ->
            ModuDetailScreen(
               name,
               close = { appState.navController.popBackStack() },
               onAction = { action: Action ->
                  when(action) {
                     is ModuAction.Share -> shareModu(action.modu)
                     is ModuAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(appState.navController)
                  }
               }
            )
         }
      }

      bottomSheet(ModuRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.MODU,
            close = { appState.navController.popBackStack() }
         )
      }

      bottomSheet(ModuRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.MODU,
            close = { appState.navController.popBackStack() }
         )
      }
   }
}