package mil.nga.msi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DrawerScreen(val title: String, val route: String) {
   object Map : DrawerScreen("Map", "MapFragment")
   object Asams : DrawerScreen("ASAMs", "asams")
   object Modus : DrawerScreen("MODUs", "modus")
   object Settings : DrawerScreen( "Settings", "settings")
}

private val screens = listOf(
   DrawerScreen.Map,
   DrawerScreen.Asams,
   DrawerScreen.Modus,
   DrawerScreen.Settings
)

@Composable
fun NavigationDrawer(
   modifier: Modifier = Modifier,
   onDestinationClicked: (route: String) -> Unit
) {
   Column(
      modifier
         .fillMaxSize()
         .padding(start = 24.dp, top = 48.dp)
   ) {
      screens.forEach { screen ->
         Spacer(Modifier.height(24.dp))
         Text(
            text = screen.title,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.clickable {
               onDestinationClicked(screen.route)
            }
         )
      }
   }
}