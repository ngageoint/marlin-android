package mil.nga.msi.ui.radiobeacon.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.NavPoint
import mil.nga.msi.ui.radiobeacon.RadioBeaconAction
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun RadioBeaconDetailScreen(
   key: RadioBeaconKey,
   close: () -> Unit,
   onAction: (RadioBeaconAction) -> Unit,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   val baseMap by viewModel.baseMap.observeAsState()
   val beacon by viewModel.getRadioBeacon(key.volumeNumber, key.featureNumber).observeAsState()
   Column {
      TopBar(
         title = RadioBeaconRoute.Detail.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      RadioBeaconDetailContent(
         beacon = beacon,
         baseMap = baseMap,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(RadioBeaconAction.Zoom(it)) },
         onShare = { onAction(RadioBeaconAction.Share(it.toString())) },
         onCopyLocation = { onAction(RadioBeaconAction.Location(it)) }
      )
   }
}

@Composable
private fun RadioBeaconDetailContent(
   beacon: RadioBeacon?,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: (NavPoint) -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (beacon != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            RadioBeaconHeader(beacon, baseMap, tileProvider, onZoom, onShare, onCopyLocation)
            RadioBeaconInformation(beacon)
         }
      }
   }
}

@Composable
private fun RadioBeaconHeader(
   beacon: RadioBeacon,
   baseMap: BaseMapType?,
   tileProvider: TileProvider,
   onZoom: (NavPoint) -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card {
      Column {
         MapClip(
            latLng = LatLng(beacon.latitude, beacon.longitude),
            baseMap = baseMap,
             tileProvider = tileProvider
         )

         Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "${beacon.featureNumber} ${beacon.volumeNumber}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            beacon.name?.let { name ->
               Text(
                  text = name,
                  style = MaterialTheme.typography.titleLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = beacon.sectionHeader,
                  style = MaterialTheme.typography.bodyMedium
               )
            }

            beacon.morseCode()?.let { code ->
               Text(
                  text = beacon.morseLetter(),
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.padding(top = 4.dp)
               )

               MorseCode(
                  text = code,
                  modifier = Modifier.padding(top = 4.dp)
               )
            }

            beacon.expandedCharacteristicWithoutCode()?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 0.dp)
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               beacon.stationRemark?.let { stationRemark ->
                  Text(
                     text = stationRemark,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 8.dp)
                  )
               }
            }

            RadioBeaconFooter(
               beacon,
               onZoom = { onZoom(NavPoint(beacon.latitude, beacon.longitude))},
               onShare = { onShare(beacon) },
               onCopyLocation)
         }
      }
   }
}

@Composable
private fun RadioBeaconFooter(
   beacon: RadioBeacon,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
   ) {
      RadioBeaconLocation(beacon.dms, onCopyLocation)
      RadioBeaconActions(onZoom, onShare)
   }
}

@Composable
private fun RadioBeaconLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun RadioBeaconActions(
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
private fun RadioBeaconInformation(
   beacon: RadioBeacon
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
      )
   }

   Card(Modifier.fillMaxWidth()) {
      val information = beacon.information()
      if (information.any { entry -> entry.value?.isNotEmpty() == true }) {
         Column(Modifier.padding(8.dp)) {
            information.forEach { entry ->
               RadioBeaconProperty(title = entry.key, value = entry.value)
            }
         }
      }
   }
}

@Composable
private fun RadioBeaconProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
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
               text = value.trim(),
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}


@Composable
private fun MorseCode(
   text: String,
   modifier: Modifier = Modifier,
) {
   Row(modifier = modifier) {
      text.split(" ").forEach { letter ->
         if (letter == "-" || letter == "•") {
            Box(
               modifier = Modifier
                  .padding(end = 8.dp)
                  .height(5.dp)
                  .width(if (letter == "-") 24.dp else 8.dp)
                  .background(MaterialTheme.colorScheme.onSurface)
            )
         }
      }
   }
}