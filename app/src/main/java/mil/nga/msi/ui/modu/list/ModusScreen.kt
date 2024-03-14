package mil.nga.msi.ui.modu.list

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.modu.ModuSummary

@Composable
fun ModusScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: ModusViewModel = hiltViewModel()
) {
   val filters by viewModel.moduFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = ModuRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort MODUs")
            }

            BadgedBox(
               badge = {
                  if (filters.isNotEmpty()) {
                     Badge(
                        containerColor = MaterialTheme.colorScheme.tertiary,
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
                     contentDescription = "Filter MODUs"
                  )
               }
            }
         }
      )

      Box(Modifier.fillMaxWidth()) {
         Modus(
            viewModel.modus,
            onTap = { onAction(ModuAction.Tap(it)) },
            onZoom = { onAction(ModuAction.Zoom(it)) },
            onShare = { onAction(ModuAction.Share(it)) },
            onBookmark = { (modu, bookmark) ->
               if (bookmark == null) {
                  onAction(Action.Bookmark(BookmarkKey.fromModu(modu)))
               } else {
                  viewModel.deleteBookmark(bookmark)
               }
            },
            onCopyLocation = { onAction(ModuAction.Location(it)) }
         )

         Box(
            Modifier
               .align(Alignment.BottomEnd)
               .padding(16.dp)
         ) {
            FloatingActionButton(
               containerColor = MaterialTheme.colorScheme.tertiaryContainer,
               onClick = { onAction(Action.Export(listOf(ExportDataSource.Modu))) }
            ) {
               Icon(Icons.Outlined.Download,
                  contentDescription = "Export MODUs as GeoPackage"
               )
            }
         }
      }
   }
}

@Composable
private fun Modus(
   pagingState: Flow<PagingData<ModuListItem>>,
   onTap: (Modu) -> Unit,
   onZoom: (Modu) -> Unit,
   onShare: (Modu) -> Unit,
   onBookmark: (ModuWithBookmark) -> Unit,
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
                  is ModuListItem.ModuItem -> it.moduWithBookmark.modu.name
                  is ModuListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is ModuListItem.ModuItem -> "modu"
                  is ModuListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is ModuListItem.ModuItem -> {
                     ModuCard(
                        moduWithBookmark = item.moduWithBookmark,
                        onTap = { onTap(item.moduWithBookmark.modu) },
                        onZoom = { onZoom(item.moduWithBookmark.modu) },
                        onShare = { onShare(item.moduWithBookmark.modu) },
                        onBookmark = { onBookmark(item.moduWithBookmark) },
                        onCopyLocation = onCopyLocation
                     )
                  }
                  is ModuListItem.HeaderItem -> {
                     Text(
                        text = item.header,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall,
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
private fun ModuCard(
   moduWithBookmark: ModuWithBookmark,
   onTap: () -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (modu, bookmark) = moduWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         ModuSummary(moduWithBookmark = moduWithBookmark)
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