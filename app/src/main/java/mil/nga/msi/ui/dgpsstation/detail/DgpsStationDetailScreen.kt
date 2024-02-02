package mil.nga.msi.ui.dgpsstation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.dgpsstation.DgpsStationViewModel
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip

@Composable
fun DgpsStationDetailScreen(
   key: DgpsStationKey,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: DgpsStationViewModel = hiltViewModel()
) {
   LaunchedEffect(key) {
      viewModel.setDgpsStationKey(key)
   }

   val tileProvider by viewModel.tileProvider.observeAsState()
   val dgpsStationWithBookmark by viewModel.dgpsStationWithBookmark.observeAsState()

   Column {
      TopBar(
         title = dgpsStationWithBookmark?.dgpsStation?.name ?: "",
         navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      DgpsStationContent(
         dgpsStationWithBookmark = dgpsStationWithBookmark,
         tileProvider = tileProvider,
         onZoom = { onAction(DgpsStationAction.Zoom(it.latLng)) },
         onShare = { onAction(DgpsStationAction.Share(it)) },
         onBookmark = { (dgpsStation, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(DgpsStationAction.Location(it)) }
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStationWithBookmark: DgpsStationWithBookmark?,
   tileProvider: TileProvider?,
   onZoom: (DgpsStation) -> Unit,
   onShare: (DgpsStation) -> Unit,
   onBookmark: (DgpsStationWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (dgpsStationWithBookmark != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            DgpsStationHeader(
               dgpsStationWithBookmark = dgpsStationWithBookmark,
               tileProvider = tileProvider,
               onZoom = { onZoom(dgpsStationWithBookmark.dgpsStation) },
               onShare = { onShare(dgpsStationWithBookmark.dgpsStation) },
               onBookmark = { onBookmark(dgpsStationWithBookmark) },
               onCopyLocation = onCopyLocation
            )
            DgpsStationInformation(dgpsStationWithBookmark.dgpsStation)
         }
      }
   }
}

@Composable
private fun DgpsStationHeader(
   dgpsStationWithBookmark: DgpsStationWithBookmark,
   tileProvider: TileProvider?,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (dgpsStation, bookmark) = dgpsStationWithBookmark

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
                  if (remarks.isNotBlank()) {
                     Text(
                        text = remarks,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                     )
                  }
               }

               BookmarkNotes(
                  notes = bookmark?.notes,
                  modifier = Modifier.padding(top = 16.dp)
               )
            }
         }

         DataSourceActions(
            latLng = dgpsStation.latLng,
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

         Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
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