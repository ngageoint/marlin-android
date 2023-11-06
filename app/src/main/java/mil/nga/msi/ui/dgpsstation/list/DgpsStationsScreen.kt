package mil.nga.msi.ui.dgpsstation.list

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.main.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DgpsStationsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: DgpsStationsViewModel = hiltViewModel()
) {
   val filters by viewModel.dgpsStationFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = DgpsStationRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort DPGS Stations")
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
                     contentDescription = "Filter DGPS Stations"
                  )
               }
            }
         }
      )

      Box(Modifier.fillMaxWidth()) {
         DgpsStations(
            pagingState = viewModel.dgpsStations,
            onTap = { onAction(DgpsStationAction.Tap(it)) },
            onZoom = { onAction(DgpsStationAction.Zoom(it.latLng)) },
            onShare = { onAction(DgpsStationAction.Share(it)) },
            onBookmark = { (dgpsStation, bookmark) ->
               if (bookmark == null) {
                  onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation)))
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
               onClick = { onAction(Action.Export(listOf(ExportDataSource.DgpsStation))) }
            ) {
               Icon(Icons.Outlined.Download,
                  contentDescription = "Export digital GPS stations as GeoPackage"
               )
            }
         }
      }
   }
}

@Composable
private fun DgpsStations(
   pagingState: Flow<PagingData<DgpsStationListItem>>,
   onTap: (DgpsStation) -> Unit,
   onZoom: (DgpsStation) -> Unit,
   onShare: (DgpsStation) -> Unit,
   onBookmark: (DgpsStationWithBookmark) -> Unit,
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
                  is DgpsStationListItem.DgpsStationItem -> it.dgpsStationWithBookmark.dgpsStation.id
                  is DgpsStationListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is DgpsStationListItem.DgpsStationItem -> "dgpsStation"
                  is DgpsStationListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is DgpsStationListItem.HeaderItem -> {
                     Text(
                        text = item.header,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                     )
                  }
                  is DgpsStationListItem.DgpsStationItem -> {
                     DgpsStationCard(
                        dgpsStationWithBookmark = item.dgpsStationWithBookmark,
                        onTap = { onTap(item.dgpsStationWithBookmark.dgpsStation) },
                        onZoom = { onZoom(item.dgpsStationWithBookmark.dgpsStation) },
                        onShare = { onShare(item.dgpsStationWithBookmark.dgpsStation) },
                        onBookmark = { onBookmark(item.dgpsStationWithBookmark) },
                        onCopyLocation = onCopyLocation
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun DgpsStationCard(
   dgpsStationWithBookmark: DgpsStationWithBookmark,
   onTap: () -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (dgpsStation, bookmark) = dgpsStationWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
         DgpsStationSummary(
            dgpsStationWithBookmark,
            modifier = Modifier.padding(bottom = 8.dp)
         )

         DataSourceActions(
            latLng = dgpsStation.latLng,
            bookmarked = bookmark != null,
            onShare = { onShare() },
            onZoom = { onZoom() },
            onBookmark = { onBookmark() },
            onCopyLocation = onCopyLocation
         )
      }
   }
}