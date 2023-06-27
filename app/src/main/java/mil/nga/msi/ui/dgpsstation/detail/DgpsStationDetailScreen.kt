package mil.nga.msi.ui.dgpsstation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel
import mil.nga.msi.ui.coordinate.CoordinateTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun DgpsStationDetailScreen(
   key: DgpsStationKey,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   val dgpsStation by viewModel.getDgpsStation(key.volumeNumber, key.featureNumber).observeAsState()

   Column {
      TopBar(
         title = dgpsStation?.name ?: "",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      DgpsStationContent(
         dgpsStation = dgpsStation,
         tileProvider = viewModel.tileProvider,
         onAction = onAction
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStation: DgpsStation?,
   tileProvider: TileProvider,
   onAction: (Action) -> Unit
) {
   if (dgpsStation != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            DgpsStationHeader(dgpsStation, tileProvider, onAction)
            DgpsStationInformation(dgpsStation)
         }
      }
   }
}

@Composable
private fun DgpsStationHeader(
   dgpsStation: DgpsStation,
   tileProvider: TileProvider,
   onAction: (Action) -> Unit
) {
   Card {
      Column {
         dgpsStation.name?.let { name ->
            Surface(
               color = DataSource.DGPS_STATION.color,
               contentColor = MaterialTheme.colorScheme.onPrimary,
               modifier = Modifier.fillMaxWidth()
            ) {
               Text(
                  text = name,
                  style = MaterialTheme.typography.headlineSmall,
                  modifier = Modifier.padding(16.dp)
               )
            }
         }

         MapClip(
            latLng = LatLng(dgpsStation.latitude, dgpsStation.longitude),
            tileProvider = tileProvider
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "${dgpsStation.featureNumber} ${dgpsStation.volumeNumber}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )

               Text(
                  text = dgpsStation.sectionHeader,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.padding(top = 8.dp)
               )

               dgpsStation.remarks?.let { remarks ->
                  Text(
                     text = remarks,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 8.dp)
                  )
               }
            }

            DgpsStationFooter(
               dgpsStation,
               onZoom = {
                  onAction(Action.Zoom(dgpsStation.latLng))
               },
               onShare = {
                  onAction(DgpsStationAction.Share(dgpsStation))
               },
               onCopyLocation = {
                  onAction(DgpsStationAction.Location(it))
               }
            )
         }
      }
   }
}

@Composable
private fun DgpsStationFooter(
   dgpsStation: DgpsStation,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      DgpsStationLocation(dgpsStation.latLng, onCopyLocation)
      DgpsStationActions(onZoom, onShare)
   }
}

@Composable
private fun DgpsStationLocation(
   latLng: LatLng,
   onCopyLocation: (String) -> Unit
) {
   CoordinateTextButton(
      latLng = latLng,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun DgpsStationActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share Radio Beacon"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to Radio Beacon"
         )
      }
   }
}


@Composable
private fun DgpsStationInformation(
   dgpsStation: DgpsStation
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
      )
   }

   Card(Modifier.fillMaxWidth()) {
      val information = dgpsStation.information()
      if (information.any { entry -> entry.value?.toString().orEmpty().isNotEmpty() }) {

         Column(Modifier.padding(8.dp)) {
            information.forEach { entry ->
               DgpsStationProperty(title = entry.key, value = entry.value)
            }
         }
      }
   }
}

@Composable
private fun DgpsStationProperty(
   title: String,
   value: Any?
) {
   if (value?.toString()?.isNotBlank() == true) {
      Column(Modifier.padding(bottom = 16.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = title,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = value.toString().trim(),
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}