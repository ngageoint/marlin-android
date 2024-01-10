package mil.nga.msi.ui.noticetomariners.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun NoticeToMarinersAllScreen(
   onTap: (Int) -> Unit,
   onBookmark: (Int) -> Unit,
   close: () -> Unit,
   viewModel: NoticeToMarinersAllViewModel = hiltViewModel()
) {
   val notices by viewModel.notices.observeAsState(emptyMap())

   Column {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(Modifier.fillMaxSize()) {
         NoticeToMarinersItems(
            notices,
            onTap = onTap,
            onBookmark = { (noticeNumber, bookmark) ->
               if (bookmark == null) {
                  onBookmark(noticeNumber)
               } else {
                  viewModel.deleteBookmark(bookmark)
               }
            }
         )
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoticeToMarinersItems(
   notices: Map<String, List<NoticeToMarinersWithBookmark>>,
   onTap: (Int) -> Unit,
   onBookmark: (NoticeToMarinersWithBookmark) -> Unit
) {
   LazyColumn {
      notices.forEach { (year, notices) ->
         stickyHeader {
            NoticeHeader(year = year)
         }

         items(notices) { noticeWithBookmark ->
            NoticeToMarinersItem(
               noticeToMarinersWithBookmark = noticeWithBookmark,
               onTap = { onTap(noticeWithBookmark.noticeNumber) },
               onBookmark = { onBookmark(noticeWithBookmark) }
            )

            Divider(Modifier.padding(horizontal = 16.dp))
         }
      }
   }
}


@Composable
private fun NoticeHeader(
   year: String
) {
   Surface {
      Box(
         modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.screenBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp)
      ) {

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
            Text(
               text = year,
               fontWeight = FontWeight.Medium,
               style = MaterialTheme.typography.bodySmall
            )
         }
      }
   }
}

@Composable
private fun NoticeToMarinersItem(
   noticeToMarinersWithBookmark: NoticeToMarinersWithBookmark,
   onTap: () -> Unit,
   onBookmark: () -> Unit,
) {
   val (noticeNumber, bookmark) = noticeToMarinersWithBookmark
   val (start, end) = NoticeToMariners.span(noticeNumber)

   ListItem(
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onTap() },
      headlineContent = {
         Text(
            text = noticeNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )
      },
      supportingContent = {
         Column {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "$start - $end",
                  style = MaterialTheme.typography.titleSmall
               )
            }

            BookmarkNotes(
               notes = bookmark?.notes,
               modifier = Modifier.padding(top = 16.dp)
            )
         }

      },
      trailingContent = {
         IconButton(onClick = { onBookmark() }) {
            Icon(
               imageVector = if (bookmark != null) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
               tint = MaterialTheme.colorScheme.tertiary,
               contentDescription = "Bookmark Notice To Mariners"
            )
         }
      }
   )
}