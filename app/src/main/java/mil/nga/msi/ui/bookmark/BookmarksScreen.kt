package mil.nga.msi.ui.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuSummary

@Composable
fun BookmarksScreen(
   openDrawer: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: BookmarksViewModel = hiltViewModel()
) {
   val bookmarks by viewModel.bookmarks.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = BookmarkRoute.List.title,
         navigationIcon = Icons.Default.Menu,
         onNavigationClicked = { openDrawer() }
      )

      if (bookmarks.isEmpty()) {
         EmptyState()
      } else {
         Bookmarks(
            bookmarks = bookmarks,
            onAction = { action ->
               if (action is Action.Bookmark) {
                  viewModel.deleteBookmark(action.key)
               } else onAction(action)
            }
         )
      }
   }
}

@Composable
private fun EmptyState() {
   Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
   ) {
      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
      ) {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
               Icons.Outlined.Bookmarks,
               modifier = Modifier
                  .size(220.dp)
                  .padding(bottom = 32.dp),
               contentDescription = "Bookmark icon"
            )

            Text(
               text = "No Bookmarks",
               style = MaterialTheme.typography.headlineMedium,
               modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
               text = "Bookmark an item and it will show up here.",
               style = MaterialTheme.typography.titleLarge,
               modifier = Modifier.padding(horizontal = 32.dp),
               textAlign = TextAlign.Center
            )
         }
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Bookmarks(
   bookmarks: List<ItemWithBookmark>,
   onAction: (Action) -> Unit
) {
   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         contentPadding = PaddingValues(vertical = 8.dp),
         modifier = Modifier.padding(8.dp)
      ) {
         items(
            count = bookmarks.count(),
            key = {
               val bookmark = bookmarks[it].bookmark
               "${bookmark.dataSource}-${bookmark.id}"
            }
         ) { index ->
            val bookmark = bookmarks[index]
            Box(Modifier.animateItemPlacement()) {
               Bookmark(
                  itemWithBookmark = bookmark,
                  onAction = onAction
               )
            }
         }
      }
   }
}

@Composable
private fun Bookmark(
   itemWithBookmark: ItemWithBookmark,
   onAction: (Action) -> Unit
) {
   when (itemWithBookmark.item) {
      is Asam -> {
         val asam = AsamWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         AsamBookmark(asamWithBookmark = asam, onAction = onAction)
      }
      is DgpsStation -> {
         val dgpsStation = DgpsStationWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         DgpsStationBookmark(dgpsStationWithBookmark = dgpsStation, onAction = onAction)
      }
      is Modu -> {
         val modu = ModuWithBookmark(itemWithBookmark.item, itemWithBookmark.bookmark)
         ModuBookmark(moduWithBookmark = modu, onAction = onAction)
      }
   }
}

@Composable fun AsamBookmark(
   asamWithBookmark: AsamWithBookmark,
   onAction: (Action) -> Unit
) {
   val (asam, bookmark) = asamWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(AsamAction.Tap(asam)) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.ASAM)

         AsamSummary(asamWithBookmark)

         bookmark?.notes?.let { notes -> BookmarkNotes(notes = notes) }

         DataSourceFooter(
            latLng = asam.latLng,
            bookmarked = bookmark != null,
            onZoom = {
               onAction(Action.Zoom(asam.latLng))
            },
            onShare = {
               onAction(AsamAction.Share(asam))
            },
            onBookmark = {
               onAction(Action.Bookmark(BookmarkKey.fromAsam(asam)))
            },
            onCopyLocation = {
               onAction(AsamAction.Location(it))
            }
         )
      }
   }
}

@Composable fun DgpsStationBookmark(
   dgpsStationWithBookmark: DgpsStationWithBookmark,
   onAction: (Action) -> Unit
) {
   val (dgpsStation, bookmark) = dgpsStationWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(DgpsStationAction.Tap(dgpsStation)) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.DGPS_STATION)
         DgpsStationSummary(dgpsStationWithBookmark)

         bookmark?.notes?.let { BookmarkNotes(notes = it) }

         DataSourceFooter(
            latLng = dgpsStation.latLng,
            bookmarked = bookmark != null,
            onZoom = {
               onAction(Action.Zoom(dgpsStation.latLng))
            },
            onShare = {
               onAction(DgpsStationAction.Share(dgpsStation))
            },
            onBookmark = {
               onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation)))
            },
            onCopyLocation = {
               onAction(ModuAction.Location(it))
            }
         )
      }
   }
}

@Composable fun ModuBookmark(
   moduWithBookmark: ModuWithBookmark,
   onAction: (Action) -> Unit
) {
   val (modu, bookmark) = moduWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(ModuAction.Tap(modu)) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.MODU)
         ModuSummary(moduWithBookmark)

         bookmark?.notes?.let { notes ->
            BookmarkNotes(notes = notes)
         }

         DataSourceFooter(
            latLng = modu.latLng,
            bookmarked = bookmark != null,
            onZoom = {
               onAction(Action.Zoom(modu.latLng))
            },
            onShare = {
               onAction(ModuAction.Share(modu))
            },
            onBookmark = {
               onAction(Action.Bookmark(BookmarkKey.fromModu(modu)))
            },
            onCopyLocation = {
               onAction(ModuAction.Location(it))
            }
         )
      }
   }
}

@Composable
private fun BookmarkNotes(
   notes: String
) {
   Column(
      Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
   ) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "BOOKMARK NOTES",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )

         Text(
            text = notes,
            style = MaterialTheme.typography.bodyMedium
         )
      }
   }
}