package mil.nga.msi

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.launch
import mil.nga.msi.ui.asam.detail.AsamDetailScreen
import mil.nga.msi.ui.asam.list.AsamsScreen
import mil.nga.msi.ui.asam.sheet.AsamSheetScreen
import mil.nga.msi.ui.map.MapScreen
import mil.nga.msi.ui.modu.list.ModusScreen

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
                     onAsamClick = { id ->
                        navController.navigate(Routes.Asam.Sheet.route + "?id=$id")
                     },
                     openDrawer = { openDrawer() }
                  )
               }
               composable(DrawerScreen.Asams.route) {
                  AsamsScreen(
                     openDrawer = { openDrawer() },
                     onAsamClick = { id ->
                        navController.navigate(Routes.Asam.Details.route + "?id=$id")
                     }
                  )
               }
               composable(DrawerScreen.Modus.route) {
                  ModusScreen(
                     openDrawer = { openDrawer() },
                     onModuClick = { name ->
                        navController.navigate(Routes.Modu.Details.route + "?id=$name")
                     }
                  )
               }
               composable("${Routes.Asam.Details.route}?id={id}") { backstackEntry ->
                  backstackEntry.arguments?.getString("id")?.let { id ->
                     AsamDetailScreen(id, close = {
                        navController.popBackStack()
                     })
                  }
               }
               bottomSheet(Routes.Asam.Sheet.route + "?id={id}") { backstackEntry ->
                  backstackEntry.arguments?.getString("id")?.let { id ->
                     AsamSheetScreen(id, onDetails = {
                        navController.navigate(Routes.Asam.Details.route + "?id=$id")
                     })
                  }
               }
            }
         }
      }
   }
}

@Composable
fun TopBar(title: String = "", buttonIcon: ImageVector, onButtonClicked: () -> Unit) {
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
