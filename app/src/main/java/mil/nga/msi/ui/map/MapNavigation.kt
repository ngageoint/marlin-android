package mil.nga.msi.ui.map

import android.net.Uri
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.settings.MapSettingsScreen
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.navigation.MapAnnotationsType
import mil.nga.msi.ui.navigation.Route
import mil.nga.msi.ui.sheet.PagingSheet

sealed class MapRoute(
   override val name: String,
   override val title: String,
): Route {
   object Map: MapRoute("map", "Map")
   object Settings: MapRoute("settings", "Map Settings")
   object PagerSheet: MapRoute("annotationPagerSheet", "Map")
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
      val latLng = backstackEntry.arguments?.getParcelable<Point?>("point")?.asLatLng()
      MapScreen(
         location = latLng,
         onAnnotationClick = { annotation ->
            when (annotation.key.type) {
               MapAnnotation.Type.ASAM ->  {
                  navController.navigate(AsamRoute.Sheet.name + "?reference=${annotation.key.id}")
               }
               MapAnnotation.Type.MODU ->  {
                  navController.navigate(ModuRoute.Sheet.name + "?name=${annotation.key.id}")
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
         openDrawer = { openNavigationDrawer() }
      )
   }

   composable(MapRoute.Settings.name) {
      bottomBarVisibility(false)

      MapSettingsScreen(onClose = {
         navController.popBackStack()
      })
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
                  navController.navigate(AsamRoute.Detail.name + "?reference=${annotation.key}")
               }
               MapAnnotation.Type.MODU -> {
                  navController.navigate(ModuRoute.Detail.name + "?name=${annotation.key}")
               }
            }
         }
      }
   }
}