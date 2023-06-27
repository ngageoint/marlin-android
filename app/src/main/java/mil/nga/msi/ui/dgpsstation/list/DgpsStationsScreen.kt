package mil.nga.msi.ui.dgpsstation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationFooter
import mil.nga.msi.ui.dgpsstation.DgpsStationSummary
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

      DgpsStations(
         pagingState = viewModel.dgpsStations,
         onAction = onAction
      )
   }
}

@Composable
private fun DgpsStations(
   pagingState: Flow<PagingData<DgpsStationListItem>>,
   onAction: (Action) -> Unit
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
                  is DgpsStationListItem.DgpsStationItem -> it.dgpsStation.id
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
                        dgpsStation = item.dgpsStation,
                        onAction = onAction
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
   dgpsStation: DgpsStation,
   onAction: (Action) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(DgpsStationAction.Tap(dgpsStation)) }
   ) {
      DgpsStationContent(
         dgpsStation,
         onAction = onAction
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStation: DgpsStation,
   onAction: (Action) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      DgpsStationSummary(dgpsStation = dgpsStation)

      DgpsStationFooter(
         dgpsStation = dgpsStation,
         onShare = {
            onAction(DgpsStationAction.Share(dgpsStation))
         },
         onZoom = {
            onAction(Action.Zoom(dgpsStation.latLng))
         },
         onBookmark = {
            onAction(Action.Bookmark(BookmarkKey.fromDgpsStation(dgpsStation)))
         },
         onCopyLocation = {
            onAction(DgpsStationAction.Location(it))
         }
      )
   }
}