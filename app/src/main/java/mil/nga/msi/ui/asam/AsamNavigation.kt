package mil.nga.msi.ui.asam

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class AsamRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
): Route {
   data object Main: AsamRoute("asams", "Anti-Shipping Activity Messages", "ASAMs")
   data object Detail: AsamRoute("asams/detail", "Anti-Shipping Activity Message Details", "ASAM Details")
   data object List: AsamRoute("asams/list", "Anti-Shipping Activity Messages", "ASAMs")
   data object Filter: AsamRoute("asams/filter", "Anti-Shipping Activity Message Filter", "ASAM Filters")
   data object Sort: AsamRoute("asams/sort", "Anti-Shipping Activity Message Sort", "ASAM Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.asamGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareAsam: (Asam) -> Unit = { asam ->
      share(Pair("Share ASAM Information", asam.toString()))
   }

   navigation(
      route = AsamRoute.Main.name,
      startDestination = AsamRoute.List.name
   ) {
      composable(
         route = AsamRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${AsamRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         AsamsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = { navController.navigate(AsamRoute.Filter.name) },
            openSort = { navController.navigate(AsamRoute.Sort.name) },
            onAction = { action ->
               when(action) {
                  is AsamAction.Share -> shareAsam(action.asam)
                  is AsamAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> { action.navigate(navController) }
               }
            }
         )
      }

      composable("${AsamRoute.Detail.name}?reference={reference}") { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.getString("reference")?.let { reference ->
            AsamDetailScreen(
               reference,
               onBack = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is AsamAction.Share -> shareAsam(action.asam)
                     is AsamAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(navController)
                  }
               }
            )
         }
      }

      bottomSheet(AsamRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.ASAM,
            close = { navController.popBackStack() }
         )
      }

      bottomSheet(AsamRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.ASAM,
            close = { navController.popBackStack() }
         )
      }
   }
}