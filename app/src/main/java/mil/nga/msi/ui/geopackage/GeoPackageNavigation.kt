package mil.nga.msi.ui.geopackage

import androidx.core.os.BundleCompat
import androidx.navigation.*
import androidx.navigation.compose.composable
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.geopackage.GeoPackageMediaKey
import mil.nga.msi.ui.action.GeoPackageFeatureAction
import mil.nga.msi.ui.geopackage.detail.GeoPackageFeatureDetailScreen
import mil.nga.msi.ui.geopackage.media.GeoPackageMediaScreen
import mil.nga.msi.ui.navigation.*

sealed class GeoPackageRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String
): Route {
   data object Detail: GeoPackageRoute("geopackage/detail", "GeoPackage Detail", "GeoPackage Detail")
   data object Media: GeoPackageRoute("geopackage/media", "GeoPackage Media", "GeoPackage Media")
}

fun NavGraphBuilder.geopackageGraph(
   appState: MarlinAppState,
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
            close = { appState.navController.popBackStack() },
            onAction = { action ->
               when(action) {
                  is GeoPackageFeatureAction.Location -> showSnackbar("${action.text} copied to clipboard")
                  else -> action.navigate(appState.navController)
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
            close = { appState.navController.popBackStack() }
         )
      }
   }
}