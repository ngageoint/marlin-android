package mil.nga.msi.ui.map.settings.layers

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.os.BundleCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.repository.preferences.Credentials
import mil.nga.msi.ui.embark.EmbarkRoute
import mil.nga.msi.ui.main.SnackbarState
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerScreen
import mil.nga.msi.ui.map.settings.layers.geopackage.MapGeoPackageLayerSettingsScreen
import mil.nga.msi.ui.map.settings.layers.grid.MapGridLayerScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerScreen
import mil.nga.msi.ui.map.settings.layers.wms.MapWMSLayerSettingsScreen
import mil.nga.msi.ui.navigation.Bounds
import mil.nga.msi.ui.navigation.NavTypeCredentials
import mil.nga.msi.ui.navigation.NavTypeLayer
import mil.nga.msi.ui.navigation.Route

sealed class MapLayerRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title
): Route {
   data object Layers: MapLayerRoute("mapLayers", "Map Layers")
   data object NewLayer: MapLayerRoute("mapNewLayer", "New Layer")
   data object CreateGridLayer: MapLayerRoute("mapCreateGridLayer", "Grid Layer")
   data object EditGridLayer: MapLayerRoute("mapEditGridLayer", "Grid Layer")
   data object WMSLayerCreateSettings: MapLayerRoute("mapWMSCreateLayerSettings", "WMS Layer")
   data object WMSLayerEditSettings: MapLayerRoute("mapWMSEditLayerSettings", "WMS Layer")
   data object WMSLayer: MapLayerRoute("mapWMSLayer", "WMS Layer")
   data object GeoPackageLayerCreateSettings: MapLayerRoute("mapGPCreateLayerSettings", "GeoPackage Layer")
   data object GeoPackageLayerEditSettings: MapLayerRoute("mapEditGPLayer", "GeoPackage Layer")
   data object GeoPackageLayer: MapLayerRoute("mapGPLayer", "GeoPackage Layer")
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
         onLayer = { layer, credentials ->
            val encodedLayer = Uri.encode(Json.encodeToString(layer))
            val encodedCredentials = credentials?.let {
               Uri.encode(Json.encodeToString(credentials))
            }

            when (layer.type) {
               LayerType.WMS -> {
                  val route = "${MapLayerRoute.WMSLayerCreateSettings.name}?layer=${encodedLayer}&credentials=${encodedCredentials}"
                  navController.navigate(route) {
                     popUpTo(route) { inclusive = true }
                  }
               }
               LayerType.TMS, LayerType.XYZ -> {
                  val route = "${MapLayerRoute.CreateGridLayer.name}?layer=${encodedLayer}&credentials=${encodedCredentials}"
                  navController.navigate(route) {
                     popUpTo(route) { inclusive = true }
                  }
               }
               LayerType.GEOPACKAGE -> {
                  val route = "${MapLayerRoute.GeoPackageLayerCreateSettings.name}?layer=${encodedLayer}"
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
      route = "${MapLayerRoute.CreateGridLayer.name}?layer={layer}&credentials={credentials}",
      arguments = listOf(
         navArgument("layer") { type = NavType.NavTypeLayer },
         navArgument("credentials") { nullable = true; type = NavType.NavTypeCredentials }
      )
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "layer", Layer::class.java)
      }
      requireNotNull(layer) { "'layer' argument is required" }

      val credentials: Credentials? = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "credentials", Credentials::class.java)
      }

      MapGridLayerScreen(
         layer = layer,
         credentials = credentials,
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
      route = "${MapLayerRoute.WMSLayerCreateSettings.name}?layer={layer}&credentials={credentials}",
      arguments = listOf(
         navArgument("layer") { type = NavType.NavTypeLayer },
         navArgument("credentials") { nullable = true; type = NavType.NavTypeCredentials }
      )
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "layer", Layer::class.java)
      }
      requireNotNull(layer) { "'layer' argument is required" }

      val credentials = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "credentials", Credentials::class.java)
      }

      MapWMSLayerSettingsScreen(
         layer = layer,
         done = {
            val encodedLayer = Uri.encode(Json.encodeToString(it))
            val encodedCredentials = Uri.encode(Json.encodeToString(credentials))
            val route = "${MapLayerRoute.WMSLayer.name}?layer=${encodedLayer}&credentials=${encodedCredentials}"
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
      route = "${MapLayerRoute.WMSLayer.name}?layer={layer}&credentials={credentials}",
      arguments = listOf(
         navArgument("layer") { type = NavType.NavTypeLayer },
         navArgument("credentials") { nullable = true; type = NavType.NavTypeCredentials }
      )
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "layer", Layer::class.java)
      }
      requireNotNull(layer) { "'layer' argument is required" }

      val credentials = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "credentials", Credentials::class.java)
      }

      MapWMSLayerScreen(
         layer = layer,
         credentials,
         onClose = {
            navController.popBackStack(MapLayerRoute.Layers.name, false)
         }
      )
   }

   composable(
      route = "${MapLayerRoute.GeoPackageLayerCreateSettings.name}?layer={layer}&import={import}",
      arguments = listOf(navArgument("layer") { type = NavType.NavTypeLayer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "layer", Layer::class.java)
      }
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
      arguments = listOf(navArgument("layer") { type = NavType.NavTypeLayer })
   ) { backstackEntry ->
      bottomBarVisibility(false)

      val layer = backstackEntry.arguments?.let { bundle ->
         BundleCompat.getParcelable(bundle, "layer", Layer::class.java)
      }
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