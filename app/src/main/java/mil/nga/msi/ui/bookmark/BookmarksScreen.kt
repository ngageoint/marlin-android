package mil.nga.msi.ui.bookmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.AsamFooter
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.main.TopBar

@Composable
fun BookmarksScreen(
   openDrawer: () -> Unit,
   onTap: (String) -> Unit,
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
         onTap = onTap,
         onAction = onAction,
         onBookmark = { viewModel.toggleBookmark(it) }
      )
   }
}

@Composable
private fun Bookmarks(
   bookmarks: List<Bookmark>,
   onTap: (String) -> Unit,
   onBookmark: (BookmarkAction) -> Unit,
   onAction: (Action) -> Unit
) {
   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         contentPadding = PaddingValues(vertical = 8.dp),
         modifier = Modifier.padding(8.dp)
      ) {
         items(
            count = bookmarks.count()
         ) { index ->
            val bookmark = bookmarks[index]
            AnimatedVisibility(
               visible = bookmark.bookmarked,
               exit = fadeOut(
                  animationSpec = TweenSpec(20000, 0, FastOutLinearInEasing)
               )
            ) {
               Bookmark(
                  bookmark = bookmark,
                  onTap = onTap,
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
   onTap: (String) -> Unit,
   onBookmark: (BookmarkAction) -> Unit,
   onAction: (Action) -> Unit
) {
   when (bookmark) {
      is Asam -> {
         AsamBookmark(
            asam = bookmark,
            onTap = onTap,
            onAction = onAction,
            onBookmark = {
               onBookmark(BookmarkAction.AsamBookmark(bookmark))
            }
         )
      }
   }
}

@Composable fun AsamBookmark(
   asam: Asam,
   onTap: (String) -> Unit,
   onBookmark: () -> Unit,
   onAction: (AsamAction) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(asam.reference) }
   ) {
      Column(Modifier.padding(vertical = 8.dp)) {
         Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
               .padding(horizontal = 16.dp)
               .size(48.dp)
         ) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
               drawCircle(color = DataSource.ASAM.color)
            })

            Image(
               painter = painterResource(id = R.drawable.ic_asam_24dp),
               modifier = Modifier.size(24.dp),
               contentDescription = "ASAM icon",
            )
         }

         AsamSummary(asam = asam)

         AsamFooter(
            asam = asam,
            onZoom = {
               onAction(AsamAction.Zoom(asam))
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