package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
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
   object GeoPackageLayer: MapLayerRoute("mapGPLayer", "GeoPackage Layer")
}

fun NavGraphBuilder.mapLayerGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit
) {
   composable(MapLayerRoute.Layers.name) {
      bottomBarVisibility(false)

      MapLayersScreen(
         onTap = { id, type ->
            val route = when (type) {
               LayerType.WMS -> {
                  "${MapLayerRoute.WMSLayerEditSettings.name}?id=${id}"
               } else -> {
                  "${MapLayerRoute.EditGridLayer.name}?id=${id}"
               }
            }

            navController.navigate(route)
         },
         onAddLayer = {
            val route = MapLayerRoute.NewLayer.name
            navController.navigate(route) {
               popUpTo(route) { inclusive = true }
            }
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
      route = "${MapLayerRoute.GeoPackageLayerCreateSettings.name}?layer={layer}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      MapGeoPackageLayerSettingsScreen(
         layer = layer,
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
      route = "${MapLayerRoute.GeoPackageLayer.name}?layer={layer}",
      arguments = listOf(navArgument("layer") { type = NavType.Layer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.getParcelable<Layer>("layer")
      requireNotNull(layer) { "'layer' argument is required" }

      MapGeoPackageLayerScreen(
         layer = layer,
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

}