package mil.nga.msi.ui.light

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.action.LightAction
import mil.nga.msi.ui.filter.FilterScreen
import mil.nga.msi.ui.light.detail.LightDetailScreen
import mil.nga.msi.ui.light.list.LightsScreen
import mil.nga.msi.ui.light.sheet.LightSheetScreen
import mil.nga.msi.ui.navigation.NavTypeLightKey
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sort.SortScreen

sealed class LightRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = DataSource.LIGHT.color
): Route {
   object Main: LightRoute("lights", "Lights")
   object Detail: LightRoute("lights/detail", "Light Details")
   object List: LightRoute("lights/list", "Lights")
   object Sheet: LightRoute("lights/sheet", "Light Sheet")
   object Filter: LightRoute("lights/filter", "Light Filters", "Light Filters")
   object Sort: LightRoute("lights/sort", "Light Sort", "Light Sort")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.lightGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit
) {
   val shareLight: (Light) -> Unit = { light ->
      share(Pair("Share Light Information", light.toString()))
   }

   navigation(
      route = LightRoute.Main.name,
      startDestination = LightRoute.List.name
   ) {
      composable(
         route = LightRoute.List.name,
         deepLinks = listOf(navDeepLink { uriPattern = "marlin://${LightRoute.List.name}" })
      ) {
         bottomBarVisibility(true)

         LightsScreen(
            openDrawer = { openNavigationDrawer() },
            openFilter = {
               navController.navigate(LightRoute.Filter.name)
            },
            openSort = {
               navController.navigate(LightRoute.Sort.name)
            },
            onAction = { action ->
               when(action) {
                  is LightAction.Share -> shareLight(action.light)
                  is LightAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(navController)
               }
            }
         )
      }

      composable(
         route = "${LightRoute.Detail.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.NavTypeLightKey })
      ) { backstackEntry ->
         bottomBarVisibility(false)

         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", LightKey::class.java)
         }?.let { key ->
            LightDetailScreen(
               key,
               close = { navController.popBackStack() },
               onAction = { action ->
                  when(action) {
                     is LightAction.Share -> shareLight(action.light)
                     is LightAction.Location -> showSnackbar("${action.text} copied to clipboard")
                     else -> action.navigate(navController)
                  }
               }
            )
         }
      }

      bottomSheet(
         route = "${LightRoute.Sheet.name}?key={key}",
         arguments = listOf(navArgument("key") { type = NavType.NavTypeLightKey })
      ) { backstackEntry ->
         backstackEntry.arguments?.let { bundle ->
            BundleCompat.getParcelable(bundle, "key", LightKey::class.java)
         }?.let { key ->
            LightSheetScreen(key, onDetails = {
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${LightRoute.Detail.name}?key=$encoded")
            })
         }
      }

      bottomSheet(LightRoute.Filter.name) {
         FilterScreen(
            dataSource = DataSource.LIGHT,
            close = {
               navController.popBackStack()
            }
         )
      }

      bottomSheet(LightRoute.Sort.name) {
         SortScreen(
            dataSource = DataSource.LIGHT,
            close = {
               navController.popBackStack()
            }
         )
      }
   }
}