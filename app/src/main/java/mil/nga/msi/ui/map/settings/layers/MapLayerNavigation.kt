package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import androidx.navigation.compose.composable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.ui.embark.EmbarkRoute
import mil.nga.msi.ui.main.SnackbarState
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerScreen
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerSettingsScreen
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerSettingsScreen
import mil.nga.msi.ui.navigation.*

sealed class MapLayerRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color.Transparent
): Route {
   object Layers: MapLayerRoute("mapLayers", "Map Layers")
   object NewLayer: MapLayerRoute("mapNewLayer", "New Layer")
   object CreateGridLayer: MapLayerRoute("mapCreateGridLayer", "Grid Layer")
   object EditGridLayer: MapLayerRoute("mapEditGridLayer", "Grid Layer")
   object WMSLayerCreateSettings: MapLayerRoute("mapWMSCreateLayerSettings", "WMS Layer")
   object WMSLayerEditSettings: MapLayerRoute("mapWMSEditLayerSettings", "WMS Layer")
   object WMSLayer: MapLayerRoute("mapWMSLayer", "WMS Layer")
   object GeoPackageLayerCreateSettings: MapLayerRoute("mapGPCreateLayerSettings", "GeoPackage Layer")
   object GeoPackageLayerEditSettings: MapLayerRoute("mapEditGPLayer", "GeoPackage Layer")
   object GeoPackageLayer: MapLayerRoute("mapGPLayer", "GeoPackage Layer")
}

fun NavGraphBuilder.mapLayerGraph(
   navController: NavController,
   showSnackbar: (SnackbarState) -> Unit,
   bottomBarVisibility: (Boolean) -> Unit
) {
   composable(MapLayerRoute.Layers.name) {
      bottomBarVisibility(false)

      MapLayersScreen(
         onTap = { id, type ->
            val route = when (type) {
               LayerType.WMS -> {
                  "${MapLayerRoute.WMSLayerEditSettings.name}?id=${id}"
               }
               LayerType.XYZ, LayerType.TMS -> {
                  "${MapLayerRoute.EditGridLayer.name}?id=${id}"
               }
               LayerType.GEOPACKAGE -> {
                  "${MapLayerRoute.GeoPackageLayerEditSettings.name}?id=${id}"
               }
            }

            navController.navigate(route)
         },
         onZoom = { bounds ->
            val encoded = Uri.encode(Json.encodeToString(Bounds.fromLatLngBounds(bounds)))
            val route = "${MapRoute.Map.name}?bounds=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onAddLayer = {
            val route = MapLayerRoute.NewLayer.name
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onDeleteLayer = { undo ->
            val snackbarState = SnackbarState(
               message = "Layer Delete",
               actionLabel = "Undo",
               actionPerformed = { undo() }
            )
            showSnackbar(snackbarState)
         },
         onClose = {
            navController.popBackStack()
         }
      )
   }

   composable(
      route = MapLayerRoute.NewLayer.name
   ) {
      bottomBarVisibility(false)

      MapNewLayerScreen(
         onLayer = { layer ->
            val encoded = Uri.encode(Json.encodeToString(layer))

            when (layer.type) {
               LayerType.WMS -> {
                  val route = "${MapLayerRoute.WMSLayerCreateSettings.name}?layer=${encoded}"
                  navController.navigate(route) {
                     popUpTo(route) { inclusive = true }
                  }
               }
               LayerType.TMS, LayerType.XYZ -> {
                  val route = "${MapLayerRoute.CreateGridLayer.name}?layer=${encoded}"
                  navController.navigate(route) {
                     popUpTo(route) { inclusive = true }
                  }
               }
               LayerType.GEOPACKAGE -> {
                  val route = "${MapLayerRoute.GeoPackageLayerCreateSettings.name}?layer=${encoded}"
                  navController.navigate(route) {
                     popUpTo(route) { inclusive = true }
                  }
               }
            }
         },
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.CreateGridLayer.name}?layer={layer}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      MapGridLayerScreen(
         layer = layer,
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable("${MapLayerRoute.EditGridLayer.name}?id={id}") { backstackEntry ->
      bottomBarVisibility(false)

      val id = backstackEntry.arguments?.getString("id")?.toLongOrNull()
      requireNotNull(id) { "'id' argument is required" }

      MapGridLayerScreen(
         id = id,
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.WMSLayerCreateSettings.name}?layer={layer}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      MapWMSLayerSettingsScreen(
         layer = layer,
         done = {
            val encoded = Uri.encode(Json.encodeToString(it))
            val route = "${MapLayerRoute.WMSLayer.name}?layer=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(route = "${MapLayerRoute.WMSLayerEditSettings.name}?id={id}") { backstackEntry ->
      bottomBarVisibility(false)

      val id = backstackEntry.arguments?.getString("id")?.toLongOrNull()
      requireNotNull(id) { "'id' argument is required" }

      MapWMSLayerSettingsScreen(
         id = id,
         done = { layer ->
            val encoded = Uri.encode(Json.encodeToString(layer))
            val route = "${MapLayerRoute.WMSLayer.name}?layer=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.WMSLayer.name}?layer={layer}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      MapWMSLayerScreen(
         layer = layer,
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.GeoPackageLayerCreateSettings.name}?layer={layer}&import={import}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      val import = backstackEntry.arguments?.getString("import")?.toBoolean() ?: false

      MapGeoPackageLayerSettingsScreen(
         layer = layer,
         done = {
            val encoded = Uri.encode(Json.encodeToString(it))
            val route = "${MapLayerRoute.GeoPackageLayer.name}?layer=${encoded}&import=${import}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(route = "${MapLayerRoute.GeoPackageLayerEditSettings.name}?id={id}") { backstackEntry ->
      bottomBarVisibility(false)

      val id = backstackEntry.arguments?.getString("id")?.toLongOrNull()
      requireNotNull(id) { "'id' argument is required" }

      MapGeoPackageLayerSettingsScreen(
         id = id,
         done = { layer ->
            val encoded = Uri.encode(Json.encodeToString(layer))
            val route = "${MapLayerRoute.GeoPackageLayer.name}?layer=${encoded}"
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
         },
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.GeoPackageLayer.name}?layer={layer}&import={import}&embark={embark}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      val import = backstackEntry.arguments?.getString("import")?.toBoolean() ?: false
      val embark = backstackEntry.arguments?.getString("embark")?.toBoolean() ?: false

      MapGeoPackageLayerScreen(
         layer = layer,
         onClose = {
            if (import) {
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
            } else {
               navController.popBackStack(MapLayerRoute.Layers.name, false)
            }
         }
      )
   }
}