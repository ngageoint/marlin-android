package mil.nga.msi.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun NavigationDrawer(
   onDestinationClicked: (route: String) -> Unit,
   viewModel: NavigationViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val tabs by viewModel.tabs.observeAsState()
   val mapped by viewModel.mapped.observeAsState()

   Column(
      modifier = Modifier
         .fillMaxHeight()
         .background(MaterialTheme.colors.screenBackground)
         .padding(top = 24.dp)
   ) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "Data Source Tabs (Drag to reorder)",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
         )
      }

      tabs?.forEach { tab ->
         val isMapped = mapped?.get(tab) ?: false

         NavigationRow(
            tab = tab,
            isMapped = isMapped,
            onMapClicked = {
               scope.launch {
                  viewModel.toggleOnMap(tab)
               }
            },
            onDestinationClicked = {
               onDestinationClicked(it.name)
            }
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "Other Data Sources (Drag to add to tabs)",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
         )
      }
   }
}

@Composable
private fun NavigationRow(
   tab: DataSource,
   isMapped: Boolean? = null,
   onMapClicked: (() -> Unit)? = null,
   onDestinationClicked: (route: Route) -> Unit
) {
   Column(
      Modifier
         .height(72.dp)
         .fillMaxWidth()
   ) {
      Row(
         modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .clickable {
               onDestinationClicked(tab.route)
            }
      ) {
         Box(
            modifier = Modifier
               .width(6.dp)
               .fillMaxHeight()
               .background(tab.color)
         )

         Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier
                  .height(72.dp)
                  .fillMaxWidth()
                  .weight(1f)
                  .padding(horizontal = 16.dp)
            ) {
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                  Text(
                     text = tab.route.title,
                     style = MaterialTheme.typography.body1,
                     fontWeight = FontWeight.Medium
                  )
               }

               isMapped?.let { mapped ->
                  var icon = Icons.Default.LocationOff
                  var iconColor = Color.Black.copy(alpha = ContentAlpha.disabled)
                  if (mapped) {
                     icon = Icons.Default.LocationOn
                     iconColor = MaterialTheme.colors.primary
                  }

                  IconButton(
                     onClick = { onMapClicked?.invoke() },
                  ) {
                     Box(
                        Modifier
                           .width(24.dp)
                           .height(24.dp)
                           .clip(CircleShape)
                           .background(iconColor)
                     )

                     Icon(
                        icon,
                        contentDescription = "Toggle On Map",
                        tint = Color.White,
                        modifier = Modifier
                           .height(16.dp)
                           .width(16.dp)
                     )
                  }
               }
            }

            Divider(Modifier.fillMaxWidth())
         }
      }
   }
}