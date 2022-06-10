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
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareAsam: (String) -> Unit = {
      share(Pair("Share ASAM Information", it))
   }

   navigation(
      route = AsamRoute.Main.name,
      startDestination = AsamRoute.List.name
   ) {
      composable(AsamRoute.List.name) {
         bottomBarVisibility(true)

         AsamsScreen(
            openDrawer = { openNavigationDrawer() },
            onAsamClick = { reference ->
               navController.navigate("${AsamRoute.Detail.name}?reference=$reference")
            },
            onShare = { shareAsam(it) },
            onCopyLocation = { location ->
               showSnackbar("$location copied to clipboard")
            }
         )
      }
      composable("${AsamRoute.Detail.name}?reference={reference}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("reference")?.let { reference ->
            AsamDetailScreen(
               reference,
               close = { navController.popBackStack() },
               onShare = { shareAsam(it) },
               onCopyLocation = { location ->
                  showSnackbar("$location copied to clipboard")
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