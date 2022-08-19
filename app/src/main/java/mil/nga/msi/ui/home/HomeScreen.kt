package mil.nga.msi.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import mil.nga.msi.ui.asam.asamGraph
import mil.nga.msi.ui.light.lightGraph
import mil.nga.msi.ui.map.mapGraph
import mil.nga.msi.ui.modu.moduGraph
import mil.nga.msi.ui.navigationalwarning.navigationalWarningGraph
import mil.nga.msi.ui.port.portGraph

fun NavGraphBuilder.homeGraph(
   navController: NavController,
   bottomBarVisibility: (Boolean) -> Unit,
   share: (Pair<String, String>) -> Unit,
   showSnackbar: (String) -> Unit,
   openNavigationDrawer: () -> Unit
) {
   mapGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
   asamGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
   moduGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
   navigationalWarningGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
   lightGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
   portGraph(
      navController = navController,
      bottomBarVisibility = { bottomBarVisibility(it) },
      share = { share(it) },
      showSnackbar = { showSnackbar(it) },
      openNavigationDrawer = { openNavigationDrawer() }
   )
}