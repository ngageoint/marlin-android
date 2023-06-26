package mil.nga.msi.ui.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.asam.AsamFooter
import mil.nga.msi.ui.datasource.DataSourceIcon
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuFooter
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.navigation.NavPoint

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

      Bookmarks(
         bookmarks = bookmarks,
         onAction = onAction,
         onBookmark = { viewModel.removeBookmark(it) }
      )
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Bookmarks(
   bookmarks: List<Bookmark>,
   onBookmark: (BookmarkAction) -> Unit,
   onAction: (Action) -> Unit
) {
   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         contentPadding = PaddingValues(vertical = 8.dp),
         modifier = Modifier.padding(8.dp)
      ) {
         items(
            count = bookmarks.count(),
            key = { bookmarks[it].bookmarkId }
         ) { index ->
            val bookmark = bookmarks[index]
            Box(Modifier.animateItemPlacement()) {
               Bookmark(
                  bookmark = bookmark,
                  onBookmark = onBookmark,
                  onAction = onAction
               )
            }
         }
      }
   }
}

@Composable
private fun Bookmark(
   bookmark: Any,
   onBookmark: (BookmarkAction) -> Unit,
   onAction: (Action) -> Unit
) {
   when (bookmark) {
      is Asam -> {
         AsamBookmark(
            asam = bookmark,
            onAction = onAction,
            onBookmark = {
               onBookmark(BookmarkAction.AsamBookmark(bookmark))
            }
         )
      }
      is Modu -> {
         ModuBookmark(
            modu = bookmark,
            onAction = onAction,
            onBookmark = {
               onBookmark(BookmarkAction.ModuBookmark(bookmark))
            }
         )
      }
   }
}

@Composable fun AsamBookmark(
   asam: Asam,
   onBookmark: () -> Unit,
   onAction: (Action) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(AsamAction.Tap(asam)) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.ASAM)
         AsamSummary(asam = asam)

         asam.bookmarkNotes?.let { notes ->
            BookmarkNotes(notes = notes)
         }

         AsamFooter(
            asam = asam,
            onZoom = {
               onAction(Action.Zoom(asam.latLng))
            },
            onShare = {
               onAction(AsamAction.Share(asam))
            },
            onBookmark = onBookmark,
            onCopyLocation = {
               onAction(AsamAction.Location(it))
            }
         )
      }
   }
}

@Composable fun ModuBookmark(
   modu: Modu,
   onBookmark: () -> Unit,
   onAction: (Action) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(ModuAction.Tap(modu)) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         DataSourceIcon(dataSource = DataSource.MODU)
         ModuSummary(modu = modu)

         modu.bookmarkNotes?.let { notes ->
            BookmarkNotes(notes = notes)
         }

         ModuFooter(
            modu = modu,
            onZoom = {
               onAction(Action.Zoom(modu.latLng))
            },
            onShare = {
               onAction(ModuAction.Share(modu))
            },
            onBookmark = onBookmark,
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