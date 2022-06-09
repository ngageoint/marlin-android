package mil.nga.msi.ui.asam

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.navigation.Route

sealed class AsamRoute(
   override val name: String,
   override val title: String,
): Route {
   object Main: AsamRoute("asams", "ASAMs")
   object Detail: AsamRoute("asams/detail", "ASAM Details")
   object List: AsamRoute("asams/list", "ASAMs")
   object Sheet: AsamRoute("asams/sheet", "ASAM Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.asamGraph(
   navController: NavController,
   openNavigationDrawer: () -> Unit
) {
   navigation(
      route = AsamRoute.Main.name,
      startDestination = AsamRoute.List.name
   ) {
      composable(AsamRoute.List.name) {
         AsamsScreen(
            openDrawer = { openNavigationDrawer() },
            onAsamClick = { reference ->
               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
            }
         )
      }
      composable("${AsamRoute.Detail.name}?reference={reference}") { backstackEntry ->
         backstackEntry.arguments?.getString("reference")?.let { reference ->
            AsamDetailScreen(reference, close = {
               navController.popBackStack()
            })
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