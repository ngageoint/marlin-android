package mil.nga.msi

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
   val navController = rememberNavController()
   Surface(color = MaterialTheme.colors.background) {
      val drawerState = rememberDrawerState(DrawerValue.Closed)
      val scope = rememberCoroutineScope()
      val openDrawer = {
         scope.launch {
            drawerState.open()
         }
      }
      ModalDrawer(
         drawerState = drawerState,
         gesturesEnabled = drawerState.isOpen,
         drawerContent = {
            Drawer(
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
            startDestination = DrawerScreen.Asams.route
         ) {
            composable(DrawerScreen.Asams.route) {
               AsamsScreen(
                  openDrawer = {
                     openDrawer()
                  }
               )
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
