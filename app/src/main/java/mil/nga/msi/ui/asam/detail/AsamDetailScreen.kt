package mil.nga.msi.ui.asam.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.AsamViewModel
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AsamDetailScreen(
   reference: String,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: AsamViewModel = hiltViewModel()
) {
   viewModel.setAsamReference(reference)
   val asamWithBookmark by viewModel.asamWithBookmark.observeAsState()

   Column {
      TopBar(
         title = "ASAM",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      AsamDetailContent(
         asamWithBookmark = asamWithBookmark,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(AsamAction.Zoom(it.latLng)) },
         onShare = { onAction(AsamAction.Share(it)) },
         onBookmark = { (asam, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromAsam(asam)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(AsamAction.Location(it)) }
      )
   }
}

@Composable
private fun AsamDetailContent(
   asamWithBookmark: AsamWithBookmark?,
   tileProvider: TileProvider,
   onZoom: (Asam) -> Unit,
   onShare: (Asam) -> Unit,
   onBookmark: (AsamWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (asamWithBookmark != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            AsamHeader(
               asamWithBookmark = asamWithBookmark,
               tileProvider = tileProvider,
               onZoom = { onZoom(asamWithBookmark.asam) },
               onShare = { onShare(asamWithBookmark.asam) },
               onBookmark = { onBookmark(asamWithBookmark) },
               onCopyLocation = onCopyLocation
            )

            AsamInformation(asamWithBookmark.asam)
         }
      }
   }
}

@Composable
private fun AsamHeader(
   asamWithBookmark: AsamWithBookmark,
   tileProvider: TileProvider,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (asam, bookmark) = asamWithBookmark

   Card {
      Column {
         Surface(
            color = DataSource.ASAM.color,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
         ) {
            val header = listOfNotNull(asam.hostility, asam.victim).joinToString(": ")
            Text(
               text = header,
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.padding(16.dp)
            )
         }

         MapClip(
            latLng = asam.latLng,
            tileProvider = tileProvider
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
               Text(
                  text = dateFormat.format(asam.date),
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )

               asam.description?.let {
                  Text(
                     text = it,
                     maxLines = 5,
                     overflow = TextOverflow.Ellipsis,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(vertical = 16.dp)
                  )
               }

               BookmarkNotes(notes = bookmark?.notes)
            }
         }

         DataSourceActions(
            latLng = asam.latLng,
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
private fun AsamInformation(
   asam: Asam
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
      Text(
         text = "ADDITIONAL INFORMATION",
         style = MaterialTheme.typography.titleMedium,
         modifier = Modifier.padding(top = 24.dp)
      )
   }

   Card(
      modifier = Modifier.padding(vertical = 8.dp)
   ) {
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
      ) {
         AsamProperty(title = "Hostility", value = asam.hostility)
         AsamProperty(title = "Victim", value = asam.victim)
         AsamProperty(title = "Reference Number", value = asam.reference)
         AsamProperty(title = "Position", value = asam.position)
         AsamProperty(title = "Navigation Area", value = asam.navigationArea)
         AsamProperty(title = "Subregion", value = asam.subregion)
      }
   }
}

@Composable
private fun AsamProperty(
   title: String,
   value: String?
) {
   if (value?.isNotBlank() == true) {
      Column(Modifier.padding(vertical = 8.dp)) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = title,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(bottom = 4.dp)
            )
         }

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
               text = value,
               style = MaterialTheme.typography.bodyLarge
            )
         }
      }
   }
}