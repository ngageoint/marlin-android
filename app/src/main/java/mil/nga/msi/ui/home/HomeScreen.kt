package mil.nga.msi.ui.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.asam.asamGraph
import mil.nga.msi.ui.dgpsstation.dgpsStationGraph
import mil.nga.msi.ui.electronicpublication.electronicPublicationGraph
import mil.nga.msi.ui.embark.EmbarkRoute
import mil.nga.msi.ui.embark.embarkGraph
import mil.nga.msi.ui.light.lightGraph
import mil.nga.msi.ui.map.AnnotationProvider
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.mapGraph
import mil.nga.msi.ui.map.settings.layers.MapLayerRoute
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerSettingsScreen
import mil.nga.msi.ui.map.settings.layers.mapLayerGraph
import mil.nga.msi.ui.modu.moduGraph
import mil.nga.msi.ui.navigationalwarning.navigationalWarningGraph
import mil.nga.msi.ui.noticetomariners.noticeToMarinersGraph
import mil.nga.msi.ui.port.portGraph
import mil.nga.msi.ui.radiobeacon.radioBeaconGraph
import mil.nga.msi.ui.report.reportGraph
import mil.nga.msi.ui.settings.settingsGraph

fun NavGraphBuilder.homeGraph(
   navController: NavController,
   embark: Boolean,
   bottomBarVisibility: (Boolean) -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit,
   openNavigationDrawer: () -> Unit,
   annotationProvider: AnnotationProvider
) {
   composable("main") {
      LaunchedEffect(embark) {
         if (embark) {
            navController.navigate(MapRoute.Map.name)
         } else {
            navController.navigate(EmbarkRoute.Welcome.name) {
               launchSingleTop = true
            }
         }
      }
   }

   composable(
      route = "geopackage",
      deepLinks = listOf(
         navDeepLink { action = Intent.ACTION_VIEW; mimeType = "*/gpkg" },
         navDeepLink { action = Intent.ACTION_VIEW; mimeType = "*/gpkx" },
         navDeepLink { action = Intent.ACTION_VIEW; mimeType = "application/octet-stream" },
         navDeepLink { action = Intent.ACTION_SEND; mimeType = "*/gpkg" },
         navDeepLink { action = Intent.ACTION_SEND; mimeType = "*/gpkx" },
         navDeepLink { action = Intent.ACTION_SEND; mimeType = "application/octet-stream" }
      )
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val intent = backstackEntry.arguments?.getParcelable<Intent?>(NavController.KEY_DEEP_LINK_INTENT)

      MapGeoPackageLayerSettingsScreen(
         uri = intent?.data,
         done = { layer ->
            val encoded = Uri.encode(Json.encodeToString(layer))
            val route = "${MapLayerRoute.GeoPackageLayer.name}?layer=${encoded}&import=true&embark=${embark}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            if (embark) {
               val route = MapRoute.Map.name
               navController.navigate(route) {
                  popUpTo(route) { inclusive = true }
               }
            } else {
               val route = EmbarkRoute.Welcome.name
               navController.navigate(EmbarkRoute.Welcome.name) {
                  popUpTo(route) { inclusive = true }
               }
            }
         }
      )
   }

   embarkGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) }
   )

   mapGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer,
      showSnackbar = { showSnackbar(it) },
      annotationProvider = annotationProvider
   )

   mapLayerGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) }
   )

   asamGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   moduGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   navigationalWarningGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   lightGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   portGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   radioBeaconGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   dgpsStationGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   electronicPublicationGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   reportGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(false) }
   )
   noticeToMarinersGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   settingsGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(false) }
   )
}