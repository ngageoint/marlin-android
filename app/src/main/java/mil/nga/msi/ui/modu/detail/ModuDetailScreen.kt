package mil.nga.msi.ui.modu.detail

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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.map.MapClip
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.modu.ModuViewModel
import mil.nga.msi.ui.theme.onSurfaceDisabled
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ModuDetailScreen(
   name: String,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: ModuViewModel = hiltViewModel()
) {
   viewModel.setName(name)
   val moduWithBookmark by viewModel.moduWithBookmark.observeAsState()

   Column {
      TopBar(
         title = "MODU",
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      ModuDetailContent(
         moduWithBookmark,
         tileProvider = viewModel.tileProvider,
         onZoom = { onAction(ModuAction.Zoom(it)) },
         onShare = { onAction(ModuAction.Share(it)) },
         onBookmark = { (modu, bookmark) ->
            if (bookmark ==  null) {
               onAction(Action.Bookmark(BookmarkKey.fromModu(modu)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(ModuAction.Location(it)) }
      )
   }
}

@Composable
private fun ModuDetailContent(
   moduWithBookmark: ModuWithBookmark?,
   tileProvider: TileProvider,
   onZoom: (Modu) -> Unit,
   onShare: (Modu) -> Unit,
   onBookmark: (ModuWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   if (moduWithBookmark != null) {
      Surface(
         modifier = Modifier.fillMaxHeight()
      ) {
         Column(
            Modifier
               .padding(all = 8.dp)
               .verticalScroll(rememberScrollState())
         ) {
            ModuHeader(
               moduWithBookmark,
               tileProvider = tileProvider,
               onZoom = { onZoom(moduWithBookmark.modu) },
               onShare = { onShare(moduWithBookmark.modu) },
               onBookmark = { onBookmark(moduWithBookmark) },
               onCopyLocation
            )
            ModuInformation(moduWithBookmark.modu)
         }
      }
   }
}

@Composable
private fun ModuHeader(
   moduWithBookmark: ModuWithBookmark,
   tileProvider: TileProvider,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit,
) {
   val (modu, bookmark) = moduWithBookmark

   Card(
      modifier = Modifier.padding(bottom = 16.dp)
   ) {
      Column {
         Surface(
            color = DataSource.MODU.color,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
         ) {
            Text(
               text = modu.name,
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.padding(16.dp)
            )
         }

         MapClip(
            latLng = LatLng(modu.latitude, modu.longitude),
            tileProvider = tileProvider
         )

         Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               modu.date.let { date ->
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

            modu.rigStatus?.let {
               Text(
                  text = it.name,
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }

            modu.specialStatus?.let {
               Text(
                  text = it,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(top = 8.dp)
               )
            }

            bookmark?.notes?.let { notes ->
               Text(
                  text = "Bookmark Notes",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
               )

               Text(
                  text = notes,
                  style = MaterialTheme.typography.bodyMedium
               )
            }

            DataSourceActions(
               latLng = modu.latLng,
               bookmarked = bookmark != null,
               onZoom = onZoom,
               onShare = onShare,
               onBookmark = onBookmark,
               onCopyLocation = onCopyLocation
            )
         }
      }
   }
}

@Composable
private fun ModuInformation(
   modu: Modu
) {
   CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
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
         ModuProperty(title = "Distance", value = modu.distance?.toString())
         ModuProperty(title = "Position", value = modu.position)
         ModuProperty(title = "Navigation Area", value = modu.navigationArea)
         ModuProperty(title = "Region", value = modu.region)
         ModuProperty(title = "Subregion", value = modu.subregion)
      }
   }
}

@Composable
private fun ModuProperty(
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