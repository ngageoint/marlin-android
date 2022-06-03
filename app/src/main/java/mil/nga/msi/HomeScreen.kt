package mil.nga.msi

import android.net.Uri
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.map.MapAnnotation
import mil.nga.msi.ui.map.MapScreen
import mil.nga.msi.ui.modu.detail.ModuDetailScreen
import mil.nga.msi.ui.modu.list.ModusScreen
import mil.nga.msi.ui.modu.sheet.ModuSheetScreen
import mil.nga.msi.ui.navigation.DrawerScreen
import mil.nga.msi.ui.navigation.MapAnnotationsType
import mil.nga.msi.ui.navigation.NavigationDrawer
import mil.nga.msi.ui.navigation.Routes
import mil.nga.msi.ui.sheet.PagingSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainScreen() {
   val scope = rememberCoroutineScope()
   val bottomSheetNavigator = rememberBottomSheetNavigator()
   val navController = rememberNavController(bottomSheetNavigator)
   val drawerState = rememberDrawerState(DrawerValue.Closed)

   ModalBottomSheetLayout(bottomSheetNavigator) {
      val openDrawer = {
         scope.launch { drawerState.open() }
      }

      Surface(color = MaterialTheme.colors.background) {
         ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
               NavigationDrawer(
                  onDestinationClicked = { route ->
                     scope.launch {
                        drawerState.close()
                     }
                     navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                           saveState = true
                        }
                        launchSingleTop = true
                     }
                  }
               )
            }
         ) {
            NavHost(
               navController = navController,
               startDestination = DrawerScreen.Map.route
            ) {
               composable(DrawerScreen.Map.route) {
                  MapScreen(
                     onAnnotationClick = { annotation ->
                        when (annotation.type) {
                           MapAnnotation.Type.ASAM ->  {
                              navController.navigate(Routes.Asam.Sheet.route + "?reference=${annotation.id}")
                           }
                           MapAnnotation.Type.MODU ->  {
                              navController.navigate(Routes.Modu.Sheet.route + "?name=${annotation.id}")
                           }
                        }
                     },
                     onAnnotationsClick = { annotations ->
                        val encoded = Uri.encode(Json.encodeToString(annotations))
                        navController.navigate(Routes.Pager.Sheet.route + "?annotations=${encoded}")
                     },
                     openDrawer = { openDrawer() }
                  )
               }
               composable(DrawerScreen.Asams.route) {
                  AsamsScreen(
                     openDrawer = { openDrawer() },
                     onAsamClick = { reference ->
                        navController.navigate(Routes.Asam.Details.route + "?reference=$reference")
                     }
                  )
               }
               composable(DrawerScreen.Modus.route) {
                  ModusScreen(
                     openDrawer = { openDrawer() },
                     onModuClick = { name ->
                        navController.navigate(Routes.Modu.Details.route + "?name=$name")
                     }
                  )
               }
               composable("${Routes.Asam.Details.route}?reference={reference}") { backstackEntry ->
                  backstackEntry.arguments?.getString("reference")?.let { reference ->
                     AsamDetailScreen(reference, close = {
                        navController.popBackStack()
                     })
                  }
               }
               composable("${Routes.Modu.Details.route}?name={name}") { backstackEntry ->
                  backstackEntry.arguments?.getString("name")?.let { name ->
                     ModuDetailScreen(name, close = {
                        navController.popBackStack()
                     })
                  }
               }
               bottomSheet(
                  Routes.Pager.Sheet.route +"?annotations={annotations}",
                  arguments = listOf(navArgument("annotations") { type = NavType.MapAnnotationsType })
               ) { backstackEntry ->
                  backstackEntry.arguments?.getParcelableArray("annotations")?.let {
                     it.toList() as? List<MapAnnotation>
                  }?.let {  annotations ->
                     PagingSheet(annotations) { annotation ->
                        when (annotation.type) {
                           MapAnnotation.Type.ASAM -> {
                              navController.navigate(Routes.Asam.Details.route + "?reference=${annotation.id}")
                           }
                           MapAnnotation.Type.MODU -> {
                              navController.navigate(Routes.Modu.Details.route + "?name=${annotation.id}")
                           }
                        }
                     }
                  }
               }
               bottomSheet(Routes.Asam.Sheet.route + "?reference={reference}") { backstackEntry ->
                  backstackEntry.arguments?.getString("reference")?.let { reference ->
                     AsamSheetScreen(reference, onDetails = {
                        navController.navigate(Routes.Asam.Details.route + "?reference=$reference")
                     })
                  }
               }
               bottomSheet(Routes.Modu.Sheet.route + "?name={name}") { backstackEntry ->
                  backstackEntry.arguments?.getString("name")?.let { name ->
                     ModuSheetScreen(name, onDetails = {
                        navController.navigate(Routes.Modu.Details.route + "?name=$name")
                     })
                  }
               }
            }
         }
      }
   }
}

@Composable
fun TopBar(title: String, buttonIcon: ImageVector, onButtonClicked: () -> Unit) {
   TopAppBar(
      title = {
         Text(
            text = title
         )
      },
      navigationIcon = {
         IconButton(onClick = { onButtonClicked() } ) {
            Icon(buttonIcon, contentDescription = "")
         }
      },
      backgroundColor = MaterialTheme.colors.primaryVariant
   )
}
