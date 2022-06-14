package mil.nga.msi.ui.map

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

@OptIn(ExperimentalMaterialNavigationApi::class, NavControllerVisibleEntries::class)
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
      var selectedAnnotation by remember { mutableStateOf<MapAnnotation?>(null) }
      val mapDestination = backstackEntry.arguments?.getParcelable<Point?>("point")?.asMapLocation(16f)

      val navBackStackEntry by navController.currentBackStackEntryAsState()
      val route = navBackStackEntry?.destination?.route
      if (route?.startsWith(AsamRoute.Sheet.name) != true &&
         route?.startsWith(ModuRoute.Sheet.name) != true) {
         selectedAnnotation = null
      }

      MapScreen(
         selectedAnnotation = selectedAnnotation,
         mapDestination = mapDestination,
         onAnnotationClick = { annotation ->
            selectedAnnotation = annotation
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
                  navController.navigate(AsamRoute.Detail.name + "?reference=${annotation.key.id}")
               }
               MapAnnotation.Type.MODU -> {
                  navController.navigate(ModuRoute.Detail.name + "?name=${annotation.key.id}")
               }
            }
         }
      }
   }
}