package mil.nga.msi.ui.radiobeacon.list

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
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.action.RadioBeaconAction
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.export.ExportDataSource
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconSummary

@Composable
fun RadioBeaconsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: RadioBeaconsViewModel = hiltViewModel()
) {
   val filters by viewModel.radioBeaconFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = RadioBeaconRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort Radio Beacons")
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
                     contentDescription = "Filter Radio Beacons"
                  )
               }
            }
         }
      )

      Box(Modifier.fillMaxWidth()) {
         RadioBeacons(
            pagingState = viewModel.radioBeacons,
            onTap = { onAction(RadioBeaconAction.Tap(it)) },
            onZoom = { onAction(RadioBeaconAction.Zoom(it.latLng)) },
            onShare = { onAction(RadioBeaconAction.Share(it)) },
            onBookmark = { (beacon, bookmark) ->
               if (bookmark == null) {
                  onAction(Action.Bookmark(BookmarkKey.fromRadioBeacon(beacon)))
               } else {
                  viewModel.deleteBookmark(bookmark)
               }
            },
            onCopyLocation = { onAction(RadioBeaconAction.Location(it)) }
         )

         Box(
            Modifier
               .align(Alignment.BottomEnd)
               .padding(16.dp)
         ) {
            FloatingActionButton(
               containerColor = MaterialTheme.colorScheme.tertiaryContainer,
               onClick = { onAction(Action.Export(listOf(ExportDataSource.RadioBeacon))) }
            ) {
               Icon(Icons.Outlined.Download,
                  contentDescription = "Export radio beacons as GeoPackage"
               )
            }
         }
      }
   }
}

@Composable
private fun RadioBeacons(
   pagingState: Flow<PagingData<RadioBeaconListItem>>,
   onTap: (RadioBeacon) -> Unit,
   onZoom: (RadioBeacon) -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onBookmark: (RadioBeaconWithBookmark) -> Unit,
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
                  is RadioBeaconListItem.RadioBeaconItem -> it.radioBeaconWithBookmark.radioBeacon.id
                  is RadioBeaconListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is RadioBeaconListItem.RadioBeaconItem -> "radioBeacon"
                  is RadioBeaconListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is RadioBeaconListItem.HeaderItem -> {
                     Text(
                        text = item.header,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                     )
                  }

                  is RadioBeaconListItem.RadioBeaconItem -> {
                     RadioBeaconCard(
                        beaconWithBookmark = item.radioBeaconWithBookmark,
                        onTap = { onTap(item.radioBeaconWithBookmark.radioBeacon) },
                        onZoom = { onZoom(item.radioBeaconWithBookmark.radioBeacon) },
                        onShare = { onShare(item.radioBeaconWithBookmark.radioBeacon) },
                        onBookmark = { onBookmark(item.radioBeaconWithBookmark) },
                        onCopyLocation = { onCopyLocation(it) }
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun RadioBeaconCard(
   beaconWithBookmark: RadioBeaconWithBookmark,
   onTap: () -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      RadioBeaconContent(
         beaconWithBookmark,
         onShare = { onShare() },
         onZoom,
         onBookmark,
         onCopyLocation
      )
   }
}

@Composable
private fun RadioBeaconContent(
   beaconWithBookmark: RadioBeaconWithBookmark,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (beacon, bookmark) = beaconWithBookmark

   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      RadioBeaconSummary(beaconWithBookmark = beaconWithBookmark)

      DataSourceActions(
         latLng = beacon.latLng,
         bookmarked = bookmark != null,
         onZoom = onZoom,
         onShare = onShare,
         onBookmark = onBookmark,
         onCopyLocation = onCopyLocation
      )
   }
}
