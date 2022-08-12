package mil.nga.msi.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.map.MapRoute
import mil.nga.msi.ui.modu.ModuRoute

private val routes = listOf(
   MapRoute.Map,
   AsamRoute.List,
   ModuRoute.List
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
      routes.forEach { route ->
         Spacer(Modifier.height(24.dp))
         Text(
            text = route.title,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.clickable {
               onDestinationClicked(route.name)
            }
         )
      }
   }
}