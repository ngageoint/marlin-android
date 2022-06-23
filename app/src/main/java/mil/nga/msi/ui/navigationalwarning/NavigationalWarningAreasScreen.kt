package mil.nga.msi.ui.navigationalwarning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.*
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningGroup
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.overlay.GeoPackageTileProvider

@Composable
fun NavigationalWarningGroupScreen(
   openDrawer: () -> Unit,
   onGroupTap: (NavigationArea) -> Unit,
   viewModel: NavigationalWarningAreasViewModel = hiltViewModel()
) {
   val scrollState = rememberScrollState()
   val geoPackageTileProvider = viewModel.naturalEarthTileProvider
   val warningsByArea by viewModel.navigationalWarningsByArea.observeAsState(emptyList())

   Column() {
      TopBar(
         title = NavigationWarningRoute.Main.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      NavigationAreaMap(geoPackageTileProvider)

      // TODO sort by nav area you are in, then alphabetical
      Column(Modifier.verticalScroll(scrollState)) {
         warningsByArea.forEach { group ->
            NavigationalWarnings(group) {
               onGroupTap(group.navigationArea)
            }

            Divider()
         }
      }
   }
}

@Composable
private fun NavigationAreaMap(
   tileProvider: GeoPackageTileProvider
) {
   GoogleMap(
      modifier = Modifier
         .height(250.dp)
         .fillMaxWidth(),
      properties = MapProperties(
         mapType = MapType.NONE
      ),
      uiSettings = MapUiSettings(
         compassEnabled = false,
         zoomControlsEnabled = false
      )
   ) {
      TileOverlay(tileProvider)
   }
}

@Composable
private fun NavigationalWarnings(
   group: NavigationalWarningGroup,
   onGroupTap: () -> Unit
) {
   Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onGroupTap() }
         .padding(horizontal = 16.dp)
   ) {
      Column(modifier = Modifier.padding(vertical = 16.dp)) {
         Text(
            text = group.navigationArea.title,
            style = MaterialTheme.typography.subtitle2
         )

         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
               text = "${group.total} Active",
               style = MaterialTheme.typography.subtitle1
            )
         }
      }

      if (group.unread > 0) {
         Badge(
            backgroundColor = MaterialTheme.colors.error.copy(alpha = .87f),
            contentColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.error),
            modifier = Modifier.padding(end = 8.dp)
         ) {
            Text(
               text = "${group.unread}",
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(2.dp)
            )
         }
      }
   }
}