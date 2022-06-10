package mil.nga.msi.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.launch
import mil.nga.msi.R
import mil.nga.msi.resource.uri
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.asam.asamGraph
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.map.mapGraph
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.modu.moduGraph
import mil.nga.msi.ui.navigation.*
import java.io.File
import java.io.FileOutputStream

sealed class Tab(val route: Route, val icon: Int) {
   object MapTab : Tab(MapRoute.Map, R.drawable.ic_outline_map_24)
   object AsamsTab : Tab(AsamRoute.List, R.drawable.ic_asam_24dp)
   object ModusTab : Tab(ModuRoute.List, R.drawable.ic_modu_24dp)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainScreen() {
   val tabs = listOf(
      Tab.MapTab,
      Tab.AsamsTab,
      Tab.ModusTab
   )

   val context: Context = LocalContext.current
   val scope = rememberCoroutineScope()
   val scaffoldState = rememberScaffoldState()
   val bottomSheetNavigator = rememberBottomSheetNavigator()
   val navController = rememberNavController(bottomSheetNavigator)
   val bottomBarVisibility = rememberSaveable { (mutableStateOf(true)) }

   val openDrawer = {
      scope.launch { scaffoldState.drawerState.open() }
   }

   val share: (Pair<String, String>) -> Unit = { pair ->
      val shareIntent = Intent.createChooser(Intent().apply {
         action = Intent.ACTION_SEND
         putExtra(Intent.EXTRA_TEXT, pair.second)
         type = "text/plain"
         putExtra(Intent.EXTRA_TITLE, pair.first)
      }, pair.first)

      context.startActivity(shareIntent)
   }

   val showSnackbar: (String) -> Unit = { message ->
      scope.launch {
         scaffoldState.snackbarHostState.showSnackbar(message)
      }
   }

   ModalBottomSheetLayout(bottomSheetNavigator) {
      Scaffold(
         scaffoldState = scaffoldState,
         drawerGesturesEnabled = false,
         drawerContent = {
            NavigationDrawer(
               onDestinationClicked = { route ->
                  scope.launch {
                     scaffoldState.drawerState.close()
                  }
                  navController.navigate(route) {
                     popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                     }
                     launchSingleTop = true
                     restoreState = true
                  }
               }
            )
         },
         bottomBar = {
            AnimatedVisibility(
               visible = bottomBarVisibility.value,
               enter = fadeIn(animationSpec = tween()),
               exit = fadeOut(animationSpec = tween()),
               content = {
                  BottomNavigation(
                     backgroundColor = MaterialTheme.colors.background
                  ) {
                     val navBackStackEntry by navController.currentBackStackEntryAsState()
                     val currentDestination = navBackStackEntry?.destination
                     tabs.forEach { tab ->
                        BottomNavigationItem(
                           icon = {
                              Icon(
                                 imageVector = ImageVector.vectorResource(id = tab.icon),
                                 contentDescription = tab.route.title
                              )
                           },
                           selectedContentColor = MaterialTheme.colors.primary,
                           unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                           label = { Text(tab.route.title) },
                           selected = currentDestination?.hierarchy?.any { it.route == tab.route.name } == true,
                           onClick = {
                              navController.navigate(tab.route.name) {
                                 popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                 }
                                 launchSingleTop = true
                                 restoreState = true
                              }
                           }
                        )
                     }
                  }
               }
            )
         }
      ) { paddingValues ->
         NavHost(
            navController = navController,
            startDestination = MapRoute.Map.name,
            modifier = Modifier.padding(paddingValues)
         ) {
            mapGraph(
               navController = navController,
               bottomBarVisibility = { bottomBarVisibility.value = it },
               openNavigationDrawer = { openDrawer() }
            )
            asamGraph(
               navController = navController,
               bottomBarVisibility = { bottomBarVisibility.value = it },
               share = { share(it) },
               showSnackbar = { showSnackbar(it) },
               openNavigationDrawer = { openDrawer() }
            )
            moduGraph(
               navController = navController,
               bottomBarVisibility = { bottomBarVisibility.value = it },
               share = { share(it) },
               showSnackbar = { showSnackbar(it) },
               openNavigationDrawer = { openDrawer() }
            )
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
