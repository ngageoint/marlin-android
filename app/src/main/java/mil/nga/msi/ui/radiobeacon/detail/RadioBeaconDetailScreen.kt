package mil.nga.msi.ui.radiobeacon.detail

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.action.RadioBeaconAction
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconViewModel
import mil.nga.msi.ui.theme.onSurfaceDisabled

@Composable
fun RadioBeaconDetailScreen(
   key: RadioBeaconKey,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: RadioBeaconViewModel = hiltViewModel()
) {
   val beaconWithBookmark by viewModel.radioBeaconWithBookmark.observeAsState()
   viewModel.setRadioBeaconKey(key)

   Column {
      TopBar(
         title = RadioBeaconRoute.Detail.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      RadioBeaconDetailContent(
         beaconWithBookmark = beaconWithBookmark,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(RadioBeaconAction.Zoom(it.latLng)) },
         onShare = { onAction(RadioBeaconAction.Share(it)) },
         onBookmark = { (beacon, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromRadioBeacon(beacon)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(RadioBeaconAction.Location(it)) }
      )
   }
}

@Composable
private fun RadioBeaconDetailContent(
   beaconWithBookmark: RadioBeaconWithBookmark?,
   tileProvider: TileProvider,
   onZoom: (RadioBeacon) -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onBookmark: (RadioBeaconWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (beaconWithBookmark != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            RadioBeaconHeader(
               beaconWithBookmark = beaconWithBookmark,
               tileProvider = tileProvider,
               onZoom = { onZoom(beaconWithBookmark.radioBeacon) },
               onShare = { onShare(beaconWithBookmark.radioBeacon) },
               onBookmark = { onBookmark(beaconWithBookmark) },
               onCopyLocation = onCopyLocation
            )

            RadioBeaconInformation(beaconWithBookmark.radioBeacon)
         }
      }
   }
}

@Composable
private fun RadioBeaconHeader(
   beaconWithBookmark: RadioBeaconWithBookmark,
   tileProvider: TileProvider,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (beacon, bookmark) = beaconWithBookmark

   Card {
      Column {
         beacon.name?.let { name ->
            Surface(
               color = DataSource.RADIO_BEACON.color,
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
            latLng = LatLng(beacon.latitude, beacon.longitude),
            tileProvider = tileProvider
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "${beacon.featureNumber} ${beacon.volumeNumber}",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = beacon.sectionHeader,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
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
                  modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
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
                     modifier = Modifier.padding(vertical = 8.dp)
                  )
               }
            }

            BookmarkNotes(notes = bookmark?.notes)
         }

         DataSourceFooter(
            latLng = beacon.latLng,
            bookmarked = bookmark != null,
            onZoom = onZoom,
            onShare = onShare,
            onBookmark = onBookmark,
            onCopyLocation = onCopyLocation,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
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
         Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
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