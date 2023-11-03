package mil.nga.msi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberMarlinAppState(
   navController: NavHostController = rememberNavController(),
): MarlinAppState {
   return remember(navController) {
      MarlinAppState(
         navController
      )
   }
}

@Stable
class MarlinAppState(
   val navController: NavController
)