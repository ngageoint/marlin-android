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
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.AsamFooter
import mil.nga.msi.ui.asam.AsamViewModel
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.navigation.NavPoint
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AsamDetailScreen(
   reference: String,
   close: () -> Unit,
   onAction: (AsamAction) -> Unit,
   viewModel: AsamViewModel = hiltViewModel()
) {
   val asam by viewModel.getAsam(reference).observeAsState()

   Column {
      TopBar(
         title = "ASAM",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      AsamDetailContent(
         asam = asam,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(AsamAction.Zoom(it)) },
         onShare = { onAction(AsamAction.Share(it)) },
         onBookmark = {
            if (it.bookmarked) {
               viewModel.removeBookmark(it)
            } else {
               onAction(AsamAction.Bookmark(BookmarkKey.fromAsam(it)))
            }
         },
         onCopyLocation = { onAction(AsamAction.Location(it)) }
      )
   }
}

@Composable
private fun AsamDetailContent(
   asam: Asam?,
   tileProvider: TileProvider,
   onZoom: (Asam) -> Unit,
   onShare: (Asam) -> Unit,
   onBookmark: (Asam) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (asam != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            AsamHeader(
               asam = asam,
               tileProvider = tileProvider,
               onZoom = { onZoom(asam) },
               onShare = { onShare(asam) },
               onBookmark = { onBookmark(asam) },
               onCopyLocation = onCopyLocation
            )
            AsamDescription(asam.description)
            AsamInformation(asam)
         }
      }
   }
}

@Composable
private fun AsamHeader(
   asam: Asam,
   tileProvider: TileProvider,
   onZoom: (NavPoint) -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
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
               asam.date.let { date ->
                  val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                  Text(
                     text = dateFormat.format(date),
                     fontWeight = FontWeight.SemiBold,
                     style = MaterialTheme.typography.labelSmall,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                  )
               }
            }

            AsamFooter(
               asam,
               onZoom = { onZoom(NavPoint(asam.latitude, asam.longitude))},
               onShare = onShare,
               onBookmark = onBookmark,
               onCopyLocation = onCopyLocation
            )
         }
      }
   }
}

@Composable
private fun AsamDescription(
   description: String?
) {
   Column(Modifier.padding(vertical = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "DESCRIPTION",
            style = MaterialTheme.typography.titleMedium
         )
      }

      Card(
         modifier = Modifier.padding(vertical = 8.dp)
      ) {
         description?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(all = 16.dp)
            )
         }
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
         style = MaterialTheme.typography.titleMedium
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