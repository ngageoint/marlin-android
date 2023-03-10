package mil.nga.msi.ui.map

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.filter.MapFilterScreen
import mil.nga.msi.ui.map.settings.MapLightSettingsScreen
import mil.nga.msi.ui.map.settings.MapSettingsScreen
import mil.nga.msi.ui.map.settings.layers.*
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerCreateScreen
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerEditScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerSettingsScreen
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.*
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.sheet.PagingSheet
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class MapRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color.Transparent
): Route {
   object Map: MapRoute("map", "Map")
   object Settings: MapRoute("mapSettings", "Map Settings")
   object Layers: MapRoute("mapLayers", "Map Layers")
   object NewLayer: MapRoute("mapNewLayer", "New Layer")
   object CreateGridLayer: MapRoute("mapCreateGridLayer", "Grid Layer")
   object EditGridLayer: MapRoute("mapEditGridLayer", "Grid Layer")
   object WMSLayerSettings: MapRoute("mapWMSLayerSettings", "WMS Layer")
   object WMSLayer: MapRoute("mapWMSLayer", "WMS Layer")
   object LightSettings: MapRoute("lightSettings", "Light Settings")
   object PagerSheet: MapRoute("annotationPagerSheet", "Map")
   object Filter: MapRoute("mapFilter", "Filters", "Filters")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.mapGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit,
   showSnackbar: (String) -> Unit,
   annotationProvider: AnnotationProvider
) {
   composable(
      route = "${MapRoute.Map.name}?point={point}",
      arguments = listOf(navArgument("point") {
            defaultValue = null
            type = NavType.Point
            nullable = true
         }
      )
   ) { backstackEntry ->
      bottomBarVisibility(true)
      val location = backstackEntry.arguments?.getParcelable<Point?>("point")?.asMapLocation(16f)
      val destination: MapPosition? = if (location != null) {
         MapPosition(location)
      } else null

      val navStackBackEntry by navController.currentBackStackEntryAsState()
      val route = navStackBackEntry?.destination?.route
      if (route?.startsWith(AsamRoute.Sheet.name) != true &&
         route?.startsWith(ModuRoute.Sheet.name) != true &&
         route?.startsWith(LightRoute.Sheet.name) != true &&
         route?.startsWith(PortRoute.Sheet.name) != true &&
         route?.startsWith(RadioBeaconRoute.Sheet.name) != true &&
         route?.startsWith(DgpsStationRoute.Sheet.name) != true
      ) {
         annotationProvider.setMapAnnotation(null)
      }

      MapScreen(
         mapDestination = destination,
         onAnnotationClick = { annotation ->
            when (annotation.key.type) {
               MapAnnotation.Type.ASAM ->  {
                  navController.navigate(AsamRoute.Sheet.name + "?reference=${annotation.key.id}")
               }
               MapAnnotation.Type.MODU ->  {
                  navController.navigate(ModuRoute.Sheet.name + "?name=${annotation.key.id}")
               }
               MapAnnotation.Type.LIGHT -> {
                  val lightKey = LightKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(lightKey))
                  navController.navigate(LightRoute.Sheet.name + "?key=${encoded}")
               }
               MapAnnotation.Type.PORT -> {
                  navController.navigate(PortRoute.Sheet.name + "?portNumber=${annotation.key.id}")
               }
               MapAnnotation.Type.RADIO_BEACON -> {
                  val beaconKey = RadioBeaconKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(beaconKey))
                  navController.navigate(RadioBeaconRoute.Sheet.name + "?key=${encoded}")
               }
               MapAnnotation.Type.DGPS_STATION -> {
                  val dgpsStationKey = DgpsStationKey.fromId(annotation.key.id)
                  val encoded = Uri.encode(Json.encodeToString(dgpsStationKey))
                  navController.navigate(DgpsStationRoute.Sheet.name + "?key=${encoded}")
               }
            }
         },
         onAnnotationsClick = { annotations ->
            val encoded = Uri.encode(Json.encodeToString(annotations))
            navController.navigate(MapRoute.PagerSheet.name + "?annotations=${encoded}")
         },
         onMapSettings = { navController.navigate(MapRoute.Settings.name) },
         openDrawer = { openNavigationDrawer() },
         openFilter = { navController.navigate(MapRoute.Filter.name) },
         locationCopy = { showSnackbar("$it copied to clipboard") }
      )
   }

   composable(MapRoute.Settings.name) {
      bottomBarVisibility(false)

      MapSettingsScreen(
         onLayers = {
            navController.navigate(MapRoute.Layers.name)
         },
         onLightSettings = {
            navController.navigate(MapRoute.LightSettings.name)
         },
         onClose = {
            navController.popBackStack()
         }
      )
   }

   composable(MapRoute.Layers.name) {
      bottomBarVisibility(false)

      MapLayersScreen(
         onTap = { id, type ->
            val route = when (type) {
               LayerType.WMS -> {
                  "${MapRoute.WMSLayerSettings.name}?id=${id}"
               } else -> {
                  "${MapRoute.EditGridLayer.name}?id=${id}"
               }
            }

            navController.navigate(route)
         },
         onAddLayer = {
            val route = MapRoute.NewLayer.name
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack()
         }
      )
   }

   composable(MapRoute.NewLayer.name) {
      bottomBarVisibility(false)

      MapNewLayerScreen(
         onGridLayer =  { type, url ->
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            val route = "${MapRoute.CreateGridLayer.name}?type=${type}&url=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onWmsLayer = { url ->
            val route = "${MapRoute.WMSLayerSettings.name}?url=${url}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapRoute.Layers.name, false)
         }
      )
   }

   composable("${MapRoute.CreateGridLayer.name}?type={type}&url={url}") { backstackEntry ->
      bottomBarVisibility(false)

      val type = backstackEntry.arguments?.getString("type")
      requireNotNull(type) { "'type' argument is required" }

      val url = backstackEntry.arguments?.getString("url")
      requireNotNull(url) { "'url' argument is required" }

      MapGridLayerCreateScreen(
         type = LayerType.valueOf(type),
         url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
         onClose = {
            navController.popBackStack(MapRoute.Layers.name, false)
         }
      )
   }

   composable("${MapRoute.EditGridLayer.name}?id={id}") { backstackEntry ->
      bottomBarVisibility(false)

      val id = backstackEntry.arguments?.getString("id")?.toLongOrNull()
      requireNotNull(id) { "'id' argument is required" }

      MapGridLayerEditScreen(
         id = id,
         onClose = {
            navController.popBackStack(MapRoute.Layers.name, false)
         }
      )
   }

   composable(route = "${MapRoute.WMSLayerSettings.name}?url={url}") { backstackEntry ->
      bottomBarVisibility(false)

      val url = backstackEntry.arguments?.getString("url")
      requireNotNull(url) { "'url' argument is required" }

      MapWMSLayerSettingsScreen(
         url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
         done = { wmsUrl ->
            val encoded = URLEncoder.encode(wmsUrl, StandardCharsets.UTF_8.toString())
            val route = "${MapRoute.WMSLayer.name}?url=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapRoute.Layers.name, false)
         }
      )
   }

   composable("${MapRoute.WMSLayer.name}?url={url}") { backstackEntry ->
      bottomBarVisibility(false)

      val url = backstackEntry.arguments?.getString("url")
      requireNotNull(url) { "'url' argument is required" }

      MapWMSLayerScreen(
         url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
         onClose = {
            navController.popBackStack(MapRoute.Layers.name, false)
         }
      )
   }

   composable(MapRoute.LightSettings.name) {
      bottomBarVisibility(false)

      MapLightSettingsScreen(
         onClose = {
            navController.popBackStack()
         }
      )
   }

   bottomSheet(
      route = "${MapRoute.PagerSheet.name}?annotations={annotations}",
      arguments = listOf(navArgument("annotations") { type = NavType.MapAnnotationsType })
   ) { backstackEntry ->
      backstackEntry.arguments?.getParcelableArray("annotations")?.let {
         it.toList() as? List<MapAnnotation>
      }?.let {  annotations ->
         PagingSheet(
            annotations,
            onDetails =  {annotation ->
               when (annotation.key.type) {
                  MapAnnotation.Type.ASAM -> {
                     navController.navigate(AsamRoute.Detail.name + "?reference=${annotation.key.id}")
                  }
                  MapAnnotation.Type.MODU -> {
                     navController.navigate(ModuRoute.Detail.name + "?name=${annotation.key.id}")
                  }
                  MapAnnotation.Type.LIGHT -> {
                     val key = LightKey.fromId(annotation.key.id)
                     val encoded = Uri.encode(Json.encodeToString(key))
                     navController.navigate(LightRoute.Detail.name + "?key=${encoded}")
                  }
                  MapAnnotation.Type.PORT -> {
                     navController.navigate(PortRoute.Detail.name + "?portNumber=${annotation.key.id}")
                  }
                  MapAnnotation.Type.RADIO_BEACON -> {
                     val key = RadioBeaconKey.fromId(annotation.key.id)
                     val encoded = Uri.encode(Json.encodeToString(key))
                     navController.navigate(RadioBeaconRoute.Detail.name + "?key=${encoded}")
                  }
                  MapAnnotation.Type.DGPS_STATION -> {
                     val key = DgpsStationKey.fromId(annotation.key.id)
                     val encoded = Uri.encode(Json.encodeToString(key))
                     navController.navigate(DgpsStationRoute.Detail.name + "?key=${encoded}")
                  }
               }
            }
         )
      }
   }

   bottomSheet(MapRoute.Filter.name) {
      MapFilterScreen(
         close = {
            navController.popBackStack()
         }
      )
   }
}