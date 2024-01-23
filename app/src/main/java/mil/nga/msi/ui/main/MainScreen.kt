package mil.nga.msi.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import kotlinx.coroutines.launch
import mil.nga.msi.R
import mil.nga.msi.ui.home.homeGraph
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.navigation.NavigationDrawer
import mil.nga.msi.ui.navigation.rememberMarlinAppState

data class SnackbarState(
   val message: String,
   val actionLabel: String? = null,
   val actionDismissed: (() -> Unit)? = null,
   val actionPerformed: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
   viewModel: MainViewModel = hiltViewModel()
) {
   val context: Context = LocalContext.current
   val scope = rememberCoroutineScope()
   val embark by viewModel.embark.observeAsState()
   val tabs by viewModel.tabs.observeAsState(emptyList())
   var bottomBarVisibility by remember { (mutableStateOf(false)) }
   val scaffoldState = rememberScaffoldState()

   val bottomSheetState = rememberModalBottomSheetState(
      initialValue = ModalBottomSheetValue.Hidden,
      skipHalfExpanded = true
   )

   val bottomSheetNavigator = remember {
      BottomSheetNavigator(sheetState = bottomSheetState)
   }

   val navController = rememberNavController(bottomSheetNavigator)
   navController.addOnDestinationChangedListener { _: NavController, destination: NavDestination, _: Bundle? ->
      viewModel.track(destination)
   }

   val appState = rememberMarlinAppState(navController)

   val openDrawer = {
      scope.launch { scaffoldState.drawerState.open() }
   }

   val share: (Intent) -> Unit = { context.startActivity(it) }

   val showSnackbar: (SnackbarState) -> Unit = { state ->
      scope.launch {
         when (scaffoldState.snackbarHostState.showSnackbar(state.message, state.actionLabel)) {
            SnackbarResult.Dismissed -> { state.actionDismissed?.invoke() }
            SnackbarResult.ActionPerformed -> { state.actionPerformed?.invoke() }
         }
      }
   }

   ModalBottomSheetLayout(bottomSheetNavigator) {
      Scaffold(
         scaffoldState = scaffoldState,
         snackbarHost = {
            SnackbarHost(it) { data ->
               Snackbar(
                  actionColor = MaterialTheme.colorScheme.primary,
                  snackbarData = data
               )
            }
         },
         drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
         backgroundColor = MaterialTheme.colorScheme.surface,
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
               visible = bottomBarVisibility,
               enter = fadeIn(animationSpec = tween()),
               exit = fadeOut(animationSpec = tween()),
               content = {
                  BottomNavigation(
                     backgroundColor = MaterialTheme.colorScheme.background
                  ) {
                     val navBackStackEntry by navController.currentBackStackEntryAsState()
                     val currentDestination = navBackStackEntry?.destination

                     BottomNavigationItem(
                        icon = {
                           Icon(
                              imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_map_24),
                              contentDescription = "Map Icon"
                           )
                        },
                        label = { Text("Map") },
                        selectedContentColor = MaterialTheme.colorScheme.tertiary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                        selected = currentDestination?.hierarchy?.any {
                           it.route?.substringBefore(
                              "?"
                           ) == MapRoute.Map.name
                        } == true,
                        onClick = {
                           if (currentDestination?.route?.substringBefore("?") != MapRoute.Map.name) {
                              navController.navigate(MapRoute.Map.name) {
                                 popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                 }
                                 launchSingleTop = true
                                 restoreState = true
                              }
                           }
                        }
                     )

                     tabs.forEach { tab ->
                        BottomNavigationItem(
                           icon = {
                              Icon(
                                 imageVector = ImageVector.vectorResource(id = tab.dataSource.icon),
                                 contentDescription = tab.route.title
                              )
                           },
                           label = {
                              Text(
                                 text = tab.route.shortTitle,
                                 maxLines = 1,
                                 overflow = TextOverflow.Ellipsis
                              )
                           },
                           selectedContentColor = MaterialTheme.colorScheme.tertiary,
                           unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(
                              alpha = ContentAlpha.disabled
                           ),
                           selected = currentDestination?.hierarchy?.any {
                              it.route?.substringBefore("?") == tab.route.name
                           } == true,
                           onClick = {
                              if (currentDestination?.route?.substringBefore("?") != tab.route.name) {
                                 navController.navigate(tab.route.name) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                       saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                 }
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
            startDestination = "main",
            modifier = Modifier.padding(paddingValues)
         ) {
            homeGraph(
               appState = appState,
               bottomBarVisibility = { visible ->
                  bottomBarVisibility = visible && tabs.isNotEmpty()
               },
               embark = embark == true,
               share = { share(it) },
               showSnackbar = { showSnackbar(it) },
               openNavigationDrawer = { openDrawer() },
               annotationProvider = viewModel.annotationProvider
            )
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
   title: String,
   navigationIcon: ImageVector? = null,
   onNavigationClicked: (() -> Unit)? = null,
   actions: @Composable RowScope.() -> Unit = {},
) {
   TopAppBar(
      title = {
         androidx.compose.material3.Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
         )
      },
      navigationIcon = {
         navigationIcon?.let { icon ->
            androidx.compose.material3.IconButton(onClick = { onNavigationClicked?.invoke() } ) {
               androidx.compose.material3.Icon(icon, contentDescription = "Navigation")
            }
         }
      },
      actions = actions,
      colors = TopAppBarDefaults.topAppBarColors(
         containerColor = MaterialTheme.colorScheme.primary,
         actionIconContentColor = Color.White,
         titleContentColor = Color.White,
         navigationIconContentColor = Color.White
      )
   )
}