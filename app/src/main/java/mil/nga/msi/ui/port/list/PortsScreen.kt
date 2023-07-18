package mil.nga.msi.ui.port.list

import android.location.Location
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
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.action.PortAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.port.PortSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: PortsViewModel = hiltViewModel()
) {
   val location by viewModel.locationProvider.observeAsState()
   val filters by viewModel.portFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = PortRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort World Ports")
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
                     contentDescription = "Filter World Ports"
                  )
               }
            }
         }
      )

      Ports(
         pagingState = viewModel.ports,
         location = location,
         onTap = { onAction(PortAction.Tap(it)) },
         onZoom = { onAction(PortAction.Zoom(it.latLng)) },
         onShare = { onAction(PortAction.Share(it)) },
         onBookmark = { (port, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromPort(port)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(AsamAction.Location(it)) }
      )
   }
}

@Composable
private fun Ports(
   pagingState: Flow<PagingData<PortListItem>>,
   location: Location?,
   onTap: (Port) -> Unit,
   onZoom: (Port) -> Unit,
   onShare: (Port) -> Unit,
   onBookmark: (PortWithBookmark) -> Unit,
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
                  is PortListItem.PortItem -> it.portWithBookmark.port.portNumber
                  is PortListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is PortListItem.PortItem -> "port"
                  is PortListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is PortListItem.PortItem -> {
                     PortCard(
                        portWithBookmark = item.portWithBookmark,
                        location = location,
                        onTap = { onTap(item.portWithBookmark.port) },
                        onZoom = { onZoom(item.portWithBookmark.port) },
                        onShare = { onShare(item.portWithBookmark.port) },
                        onBookmark = { onBookmark(item.portWithBookmark) },
                        onCopyLocation = onCopyLocation
                     )
                  }
                  is PortListItem.HeaderItem -> {
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
private fun PortCard(
   portWithBookmark: PortWithBookmark,
   location: Location?,
   onTap: () -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (port, bookmark) = portWithBookmark

   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      PortSummary(
         portWithBookmark = portWithBookmark,
         location = location,
         modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
      )

      DataSourceActions(
         latLng = port.latLng,
         bookmarked = bookmark != null,
         onZoom = onZoom,
         onShare = onShare,
         onBookmark = onBookmark,
         onCopyLocation = onCopyLocation
      )
   }
}