package mil.nga.msi.ui.modu

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
import mil.nga.msi.ui.modu.detail.ModuDetailScreen
import mil.nga.msi.ui.modu.list.ModusScreen
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class ModuRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = Color(0xFF0042A4)
): Route {
   object Main: ModuRoute("modus", "Mobile Offshore Drilling Units", "MODUs")
   object Detail: ModuRoute("modus/detail", "Mobile Offshore Drilling Unit Details", "MODU Details")
   object List: ModuRoute("modus/list", "Mobile Offshore Drilling Units", "MODUs")
   object Filter: ModuRoute("modus/filter", "Mobile Offshore Drilling Units Filters", "MODU Filters")
   object Sort: ModuRoute("modus/sort", "Mobile Offshore Drilling Units Sort", "MODU Sort")
   object Sheet: ModuRoute("modus/sheet", "Mobile Offshore Drilling Unit Sheet", "Modu Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.moduGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareModu: (String) -> Unit = {
      share(Pair("Share MODU Information", it))
   }

   val zoomTo: (Point) -> Unit = { point ->
      val encoded = Uri.encode(Json.encodeToString(point))
      navController.navigate(MapRoute.Map.name + "?point=${encoded}")
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
            openFilter = {
               navController.navigate(ModuRoute.Filter.name)
            },
            openSort = {
               navController.navigate(ModuRoute.Sort.name)
            },
            onTap = { name ->
               navController.navigate( "${ModuRoute.Detail.name}?name=$name")
            },
            onAction = { action ->
               when(action) {
                  is ModuAction.Zoom -> zoomTo(action.point)
                  is ModuAction.Share -> shareModu(action.text)
                  is ModuAction.Location -> showSnackbar("${action.text} copied to clipboard")
               }
            }
         )
      }
      composable("${ModuRoute.Detail.name}?name={name}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("name")?.let { name ->
            ModuDetailScreen(
               name,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is ModuAction.Zoom -> zoomTo(action.point)
                     is ModuAction.Share -> shareModu(action.text)
                     is ModuAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  }
               }
            )
         }
      }
      bottomSheet("${ModuRoute.Sheet.name}?name={name}") { backstackEntry ->
         backstackEntry.arguments?.getString("name")?.let { name ->
            ModuSheetScreen(name, onDetails = {
               navController.navigate("${ModuRoute.Detail.name}?name=$name")
            })
         }
      }
      bottomSheet(ModuRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.MODU,
            close = {
               navController.popBackStack()
            }
         )
      }
      bottomSheet(ModuRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.MODU,
            close = {
               navController.popBackStack()
            }
         )
      }
   }
}