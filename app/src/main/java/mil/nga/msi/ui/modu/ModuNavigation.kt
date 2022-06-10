package mil.nga.msi.ui.modu

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.ui.modu.detail.ModuDetailScreen
import mil.nga.msi.ui.modu.list.ModusScreen
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.navigation.Route

sealed class ModuRoute(
   override val name: String,
   override val title: String,
): Route {
   object Main: ModuRoute("modus", "MODUs")
   object Detail: ModuRoute("modus/detail", "MODU Details")
   object List: ModuRoute("modus/list", "MODUs")
   object Sheet: ModuRoute("modus/sheet", "Modu Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.moduGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   showSnackbar: (String) -> Unit
) {
   navigation(
      route = ModuRoute.Main.name,
      startDestination = ModuRoute.List.name,
   ) {
      composable(ModuRoute.List.name) {
         bottomBarVisibility(true)

         ModusScreen(
            openDrawer = { openNavigationDrawer() },
            onModuClick = { name ->
               navController.navigate( "${ModuRoute.Detail.name}?name=$name")
            },
            onCopyLocation = { location ->
               showSnackbar("$location copied to clipboard")
            }
         )
      }
      composable("${ModuRoute.Detail.name}?name={name}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("name")?.let { name ->
            ModuDetailScreen(
               name,
               close = {
                  navController.popBackStack()
               },
               onCopyLocation = { location ->
                  showSnackbar("$location copied to clipboard")
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
   }
}