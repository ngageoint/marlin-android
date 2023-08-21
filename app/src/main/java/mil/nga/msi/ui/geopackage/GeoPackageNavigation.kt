package mil.nga.msi.ui.geopackage

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
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.action.GeoPackageFeatureAction
import mil.nga.msi.ui.geopackage.detail.GeoPackageFeatureDetailScreen
import mil.nga.msi.ui.geopackage.media.GeoPackageMediaScreen
import mil.nga.msi.ui.geopackage.sheet.GeoPackageFeatureSheetScreen
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.*

sealed class GeoPackageRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String,
   override val color: Color = DataSource.GEOPACKAGE.color
): Route {
   object Detail: GeoPackageRoute("geopackage/detail", "GeoPackage Detail", "GeoPackage Detail")
   object Media: GeoPackageRoute("geopackage/media", "GeoPackage Media", "GeoPackage Media")
   object Sheet: GeoPackageRoute("geopackage/sheet", "GeoPackage Sheet", "GeoPackage Sheet")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.geopackageGraph(
   navController: NavController,
   showSnackbar: (String) -> Unit
) {
   composable(
      route = "${GeoPackageRoute.Detail.name}?key={key}",
      arguments = listOf(navArgument("key") { type = NavType.GeoPackageFeature })
   ) { backstackEntry ->
      backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "key", GeoPackageFeatureKey::class.java)
      }?.let { key ->
         GeoPackageFeatureDetailScreen(
            key = key,
            close = {
               navController.popBackStack()
            },
            onAction = { action ->
               when(action) {
                  is GeoPackageFeatureAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(navController)
               }
            }
         )
      }
   }

   composable(
      route = "${GeoPackageRoute.Media.name}?key={key}",
      arguments = listOf(navArgument("key") { type = NavType.GeoPackageMedia })
   ) { backstackEntry ->
      backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "key", GeoPackageMediaKey::class.java)
      }?.let { key ->
         GeoPackageMediaScreen(
            key = key,
            close = {
               navController.popBackStack()
            }
         )
      }
   }

   bottomSheet(
      route = "${GeoPackageRoute.Sheet.name}?key={key}",
      arguments = listOf(navArgument("key") { type = NavType.GeoPackageFeature })
   ) { backstackEntry ->
      backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "key", GeoPackageFeatureKey::class.java)
      }?.let { key ->
         GeoPackageFeatureSheetScreen(
            key = key,
            onDetails = {
               val encoded = Uri.encode(Json.encodeToString(key))
               navController.navigate( "${GeoPackageRoute.Detail.name}?key=$encoded")
            }
         )
      }
   }
}