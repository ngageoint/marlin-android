package mil.nga.msi.ui.asam.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.main.TopBar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsamsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: AsamsViewModel = hiltViewModel()
) {
   val filters by viewModel.asamFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = AsamRoute.List.title,
         navigationIcon = Icons.Default.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort ASAMs")
            }

            BadgedBox(
               badge = {
                  if (filters.isNotEmpty()) {
                     Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.offset(x = (-12).dp, y = 12.dp)
                     ) {
                        Text("${filters.size}")
                     }
                  }
               },
               modifier = Modifier.padding(end = 16.dp)
            ) {
               IconButton(
                  onClick = { openFilter() }
               ) {
                  Icon(
                     Icons.Default.FilterList,
                     contentDescription = "Filter ASAMs"
                  )
               }
            }
         }
      )

      Box(Modifier.fillMaxWidth()) {
         Asams(
            pagingState = viewModel.asams,
            onTap = { onAction(AsamAction.Tap(it)) },
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

         Box(
            Modifier
               .align(Alignment.BottomEnd)
               .padding(16.dp)
         ) {
            FloatingActionButton(
               containerColor = MaterialTheme.colorScheme.tertiaryContainer,
               onClick = { onAction(Action.Export(listOf(ExportDataSource.Asam))) }
            ) {
               Icon(Icons.Outlined.Download,
                  contentDescription = "Export ASAMs as GeoPackage"
               )
            }
         }
      }
   }
}

@Composable
private fun Asams(
   pagingState: Flow<PagingData<AsamListItem>>,
   onTap: (Asam) -> Unit,
   onZoom: (Asam) -> Unit,
   onShare: (Asam) -> Unit,
   onBookmark: (AsamWithBookmark) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val lazyItems = pagingState.collectAsLazyPagingItems()

   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(
            count = lazyItems.itemCount,
            key = lazyItems.itemKey {
               when (it) {
                  is AsamListItem.AsamItem -> it.asamWithBookmark.asam.reference
                  is AsamListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is AsamListItem.AsamItem -> "asam"
                  is AsamListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is AsamListItem.AsamItem -> {
                     AsamCard(
                        asamWithBookmark = item.asamWithBookmark,
                        onTap = { onTap(item.asamWithBookmark.asam) },
                        onZoom = { onZoom(item.asamWithBookmark.asam) },
                        onShare = { onShare(item.asamWithBookmark.asam) },
                        onBookmark = { onBookmark(item.asamWithBookmark) },
                        onCopyLocation = onCopyLocation
                     )
                  }
                  is AsamListItem.HeaderItem -> {
                     Text(
                        text = item.header,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun AsamCard(
   asamWithBookmark: AsamWithBookmark,
   onTap: () -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit)
{
   val (asam, bookmark) = asamWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         AsamSummary(
            asamWithBookmark,
            modifier = Modifier.padding(bottom = 8.dp)
         )

         DataSourceActions(
            latLng = asam.latLng,
            bookmarked = bookmark != null,
            onZoom = onZoom,
            onShare = onShare,
            onBookmark = onBookmark,
            onCopyLocation = onCopyLocation
         )
      }
   }
}