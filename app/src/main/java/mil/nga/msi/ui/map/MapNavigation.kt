package mil.nga.msi.ui.map

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.core.os.BundleCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.geopackage.GeoPackageFeatureKey
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.geopackage.GeoPackageRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.filter.MapFilterScreen
import mil.nga.msi.ui.map.settings.MapLightSettingsScreen
import mil.nga.msi.ui.map.settings.MapSettingsScreen
import mil.nga.msi.ui.map.settings.layers.*
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.*
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.route.list.RouteRoute
import mil.nga.msi.ui.sheet.BottomSheet

sealed class MapRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title
): Route {
   data object Map: MapRoute("map", "Map")
   data object Settings: MapRoute("mapSettings", "Map Settings")
   data object WMSLayer: MapRoute("mapWMSLayer", "WMS Layer")
   data object LightSettings: MapRoute("lightSettings", "Light Settings")
   data object PagerSheet: MapRoute("annotationPagerSheet", "Map")
   data object Filter: MapRoute("mapFilter", "Filters", "Filters")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.mapGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   showSnackbar: (String) -> Unit,
   share: (Pair<String, String>) -> Unit,
   annotationProvider: AnnotationProvider
) {
   composable(
      route = "${MapRoute.Map.name}?point={point}&bounds={bounds}",
      arguments = listOf(
         navArgument("point") {
            defaultValue = null
            type = NavType.NavTypePoint
            nullable = true
         },
         navArgument("bounds") {
            defaultValue = null
            type = NavType.NavTypeBounds
            nullable = true
         }
      )
   ) { backstackEntry ->
      bottomBarVisibility(true)
      val location = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "point", NavPoint::class.java)?.asMapLocation(16f)
      }
      val latLngBounds = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "bounds", Bounds::class.java)?.asLatLngBounds()
      }
      val destination = if (location != null) {
         MapPosition(location = location)
      } else if (latLngBounds != null) {
         MapPosition(bounds = latLngBounds)
      } else null

      val navStackBackEntry by appState.navController.currentBackStackEntryAsState()
      if (navStackBackEntry?.destination?.route?.startsWith(MapRoute.Map.name) == true) {
         annotationProvider.setMapAnnotation(null)
      }

      MapScreen(
         mapDestination = destination,
         onMapTap = { appState.navController.navigate(MapRoute.PagerSheet.name) },
         onExport = { dataSources ->
            val exportDataSources = dataSources.mapNotNull { ExportDataSource.fromDataSource(it) }
            Action.Export(exportDataSources).navigate(appState.navController)
         },
         onMapSettings = { appState.navController.navigate(MapRoute.Settings.name) },
         openDrawer = { openNavigationDrawer() },
         openFilter = { appState.navController.navigate(MapRoute.Filter.name) },
         locationCopy = { showSnackbar("$it copied to clipboard") }
      )
   }

   composable(MapRoute.Settings.name) {
      bottomBarVisibility(false)

      MapSettingsScreen(
         onLayers = { appState.navController.navigate(MapLayerRoute.Layers.name) },
         onLightSettings = { appState.navController.navigate(MapRoute.LightSettings.name) },
         onClose = { appState.navController.popBackStack() }
      )
   }

   composable(MapRoute.LightSettings.name) {
      bottomBarVisibility(false)

      MapLightSettingsScreen(
         onClose = {
            appState.navController.popBackStack()
         }
      )
   }

   bottomSheet(MapRoute.PagerSheet.name) {
      BottomSheet(
         onDetails =  { annotation ->
            when (annotation.key.type) {
               MapAnnotation.Type.ASAM -> {
                  appState.navController.navigate(AsamRoute.Detail.name + "?reference=${annotation.key.id}")
               }
               MapAnnotation.Type.MODU -> {
                  appState.navController.navigate(ModuRoute.Detail.name + "?name=${annotation.key.id}")
               }
               MapAnnotation.Type.LIGHT -> {
                  val key = LightKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  appState.navController.navigate(LightRoute.Detail.name + "?key=${encoded}")
               }
               MapAnnotation.Type.PORT -> {
                  appState.navController.navigate(PortRoute.Detail.name + "?portNumber=${annotation.key.id}")
               }
               MapAnnotation.Type.RADIO_BEACON -> {
                  val key = RadioBeaconKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  appState.navController.navigate(RadioBeaconRoute.Detail.name + "?key=${encoded}")
               }
               MapAnnotation.Type.DGPS_STATION -> {
                  val key = DgpsStationKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  appState.navController.navigate(DgpsStationRoute.Detail.name + "?key=${encoded}")
               }
               MapAnnotation.Type.NAVIGATIONAL_WARNING -> {
                  val key = NavigationalWarningKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  appState.navController.navigate(NavigationWarningRoute.Detail.name + "?key=${encoded}")
               }
               MapAnnotation.Type.GEOPACKAGE -> {
                  val key = GeoPackageFeatureKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(key))
                  appState.navController.navigate(GeoPackageRoute.Detail.name + "?key=${encoded}")
               }
               MapAnnotation.Type.ROUTE -> {
                  appState.navController.navigate(RouteRoute.Detail.name + "?routeId=${annotation.key.id}")
               }
            }
         },
         onShare = { share(it) },
         onBookmark = { key -> Action.Bookmark(key).navigate(appState.navController) }
      )
   }

   bottomSheet(MapRoute.Filter.name) {
      MapFilterScreen(
         close = { appState.navController.popBackStack() }
      )
   }
}