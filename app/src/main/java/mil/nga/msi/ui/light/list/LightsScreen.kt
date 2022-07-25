package mil.nga.msi.ui.light.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.ui.light.LightAction
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun LightsScreen(
   openDrawer: () -> Unit,
   onTap: (Light) -> Unit,
   onAction: (LightAction) -> Unit,
   viewModel: LightsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val lights by viewModel.lights.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = LightRoute.List.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      Lights(
         lights = lights,
         onTap = onTap,
         onCopyLocation = { onAction(LightAction.Location(it)) },
         onZoom = { onAction(LightAction.Zoom(it)) },
         onShare = { light ->
            scope.launch {
               viewModel.getLight(light.volumeNumber, light.featureNumber, light.characteristicNumber)?.let {
                  onAction(LightAction.Share(it.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun Lights(
   lights: List<Light>,
   onTap: (Light) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (Light) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val groupedItems = lights.groupBy { it.sectionHeader }

   Surface(
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {
      LazyColumn(
         contentPadding = PaddingValues(horizontal = 8.dp)
      ) {
         groupedItems.forEach { (section, lights) ->
            item {
               Text(
                  text = section,
                  fontWeight = FontWeight.Medium,
                  style = MaterialTheme.typography.caption,
                  modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
               )
            }

            items(lights) { light ->
               LightCard(
                  item = light,
                  onTap = { onTap(light) },
                  onShare = { onShare(light) },
                  onZoom = { onZoom(Point(light.latitude, light.longitude)) },
                  onCopyLocation = onCopyLocation
               )
            }
         }
      }
   }
}

@Composable
private fun LightCard(
   item: Light?,
   onTap: () -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap() }
      ) {
         LightContent(
            item,
            onShare,
            onZoom,
            onCopyLocation
         )
      }
   }
}

@Composable
private fun LightContent(
   item: Light,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "${item.featureNumber} ${item.internationalFeature ?: ""} ${item.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.overline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      item.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.structure?.let { structure ->
            Text(
               text = structure,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      LightFooter(
         item = item,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun LightFooter(
   item: Light,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      LightLocation(item.dms, onCopyLocation)
      LightActions(onShare, onZoom)
   }
}

@Composable
private fun LightLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun LightActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Light"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Light"
         )
      }
   }
}