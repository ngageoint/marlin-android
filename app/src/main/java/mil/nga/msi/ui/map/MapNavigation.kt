package mil.nga.msi.ui.map

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
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
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.MapAnnotationsType
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.sheet.PagingSheet

sealed class MapRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color.Transparent
): Route {
   object Map: MapRoute("map", "Map")
   object Settings: MapRoute("mapSettings", "Map Settings")
   object LightSettings: MapRoute("lightSettings", "Light Settings")
   object PagerSheet: MapRoute("annotationPagerSheet", "Map")
   object Filter: MapRoute("mapFilter", "Filters", "Filters")
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.mapGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   openNavigationDrawer: () -> Unit
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
      val mapDestination = backstackEntry.arguments?.getParcelable<Point?>("point")?.asMapLocation(16f)

      MapScreen(
         mapDestination = mapDestination,
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
         onMapSettings = {
            navController.navigate(MapRoute.Settings.name)
         },
         openDrawer = { openNavigationDrawer() },
         openFilter = {
            navController.navigate(MapRoute.Filter.name)
         }
      )
   }

   composable(MapRoute.Settings.name) {
      bottomBarVisibility(false)

      MapSettingsScreen(
         onLightSettings = {
            navController.navigate(MapRoute.LightSettings.name)
         },
         onClose = {
            navController.popBackStack()
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
         PagingSheet(annotations) { annotation ->
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
      }
   }

   bottomSheet(MapRoute.Filter.name) {
      MapFilterScreen(
         onTap = {
            when (it) {
              DataSource.ASAM -> { AsamRoute.Filter.name}
              else -> null
            }?.let { route ->
               navController.navigate(route) {
                  navController.popBackStack()
               }
            }
         },
         close = {
            navController.popBackStack()
         }
      )
   }
}