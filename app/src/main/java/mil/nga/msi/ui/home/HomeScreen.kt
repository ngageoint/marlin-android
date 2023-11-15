package mil.nga.msi.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.core.os.BundleCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.about.settingsGraph
import mil.nga.msi.ui.asam.asamGraph
import mil.nga.msi.ui.bookmark.bookmarksGraph
import mil.nga.msi.ui.dgpsstation.dgpsStationGraph
import mil.nga.msi.ui.electronicpublication.electronicPublicationGraph
import mil.nga.msi.ui.embark.EmbarkRoute
import mil.nga.msi.ui.embark.embarkGraph
import mil.nga.msi.ui.export.exportGraph
import mil.nga.msi.ui.geopackage.geopackageGraph
import mil.nga.msi.ui.light.lightGraph
import mil.nga.msi.ui.main.SnackbarState
import mil.nga.msi.ui.map.AnnotationProvider
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.mapGraph
import mil.nga.msi.ui.map.settings.layers.MapLayerRoute
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerSettingsScreen
import mil.nga.msi.ui.map.settings.layers.mapLayerGraph
import mil.nga.msi.ui.modu.moduGraph
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigationalwarning.navigationalWarningGraph
import mil.nga.msi.ui.noticetomariners.noticeToMarinersGraph
import mil.nga.msi.ui.port.portGraph
import mil.nga.msi.ui.radiobeacon.radioBeaconGraph
import mil.nga.msi.ui.report.reportGraph
import mil.nga.msi.ui.route.list.routesGraph

fun NavGraphBuilder.homeGraph(
   appState: MarlinAppState,
   embark: Boolean,
   bottomBarVisibility: (Boolean) -> Unit,
   share: (Intent) -> Unit,
   showSnackbar: (SnackbarState) -> Unit,
   openNavigationDrawer: () -> Unit,
   annotationProvider: AnnotationProvider
) {

   val shareDataSource: (Pair<String, String>) -> Unit = { (title, text) ->
      val shareIntent = Intent.createChooser(Intent().apply {
         action = Intent.ACTION_SEND
         putExtra(Intent.EXTRA_TITLE, title)
         putExtra(Intent.EXTRA_TEXT, text)
         type = "text/*"
      }, title)

      share(shareIntent)
   }

   composable("main") {
      LaunchedEffect(embark) {
         if (embark) {
            appState.navController.navigate(MapRoute.Map.name)
         } else {
            appState.navController.navigate(EmbarkRoute.Welcome.name) {
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

      val intent = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
      }

      MapGeoPackageLayerSettingsScreen(
         uri = intent?.data,
         done = { layer ->
            val encoded = Uri.encode(Json.encodeToString(layer))
            val route = "${MapLayerRoute.GeoPackageLayer.name}?layer=${encoded}&import=true&embark=${embark}"
            appState.navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            if (embark) {
               val route = MapRoute.Map.name
               appState.navController.navigate(route) {
                  popUpTo(route) { inclusive = true }
               }
            } else {
               val route = EmbarkRoute.Welcome.name
               appState.navController.navigate(EmbarkRoute.Welcome.name) {
                  popUpTo(route) { inclusive = true }
               }
            }
         }
      )
   }

   embarkGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) }
   )

   mapGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer,
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      share = { shareDataSource(it) },
      annotationProvider = annotationProvider
   )

   mapLayerGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      showSnackbar = { showSnackbar(it) }
   )

   asamGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )
   moduGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )

   navigationalWarningGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      openNavigationDrawer = openNavigationDrawer
   )

   lightGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )

   portGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )

   radioBeaconGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )

   dgpsStationGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )

   electronicPublicationGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer
   )

   reportGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(false) }
   )

   geopackageGraph(
      appState = appState,
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
   )

   noticeToMarinersGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer
   )

   bookmarksGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { shareDataSource(it) },
      showSnackbar = { showSnackbar(SnackbarState(message = it)) },
      openNavigationDrawer = openNavigationDrawer
   )
   routesGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = openNavigationDrawer
   )
   exportGraph(
      appState = appState,
      share = { uri ->
         share(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/octet-stream"
         }, null))
      },
      bottomBarVisibility = { bottomBarVisibility(it) }
   )

   settingsGraph(
      appState = appState,
      bottomBarVisibility = { bottomBarVisibility(false) }
   )
}