package mil.nga.msi.ui.dgpsstation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DgpsStationsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onTap: (DgpsStationKey) -> Unit,
   onAction: (DgpsStationAction) -> Unit,
   viewModel: DgpsStationsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
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
         onTap = { onTap(DgpsStationKey.fromDgpsStation(it)) },
         onCopyLocation = { onAction(DgpsStationAction.Location(it)) },
         onZoom = { onAction(DgpsStationAction.Zoom(it)) },
         onShare = { dgpsStation ->
            scope.launch {
               viewModel.getDgpsStation(dgpsStation.volumeNumber, dgpsStation.featureNumber)?.let {
                  onAction(DgpsStationAction.Share(it.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun DgpsStations(
   pagingState: Flow<PagingData<DgpsStationListItem>>,
   onTap: (DgpsStation) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (DgpsStation) -> Unit,
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
            when (val item = lazyItems[index]) {
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
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.dgpsStation.latitude, item.dgpsStation.longitude)) },
                     onShare = onShare
                  )
               }
               else -> { /* TODO item is null, display placeholder */}
            }
         }
      }
   }
}

@Composable
private fun DgpsStationCard(
   dgpsStation: DgpsStation,
   onTap: (DgpsStation) -> Unit,
   onShare: (DgpsStation) -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(dgpsStation) }
   ) {
      DgpsStationContent(
         dgpsStation,
         onShare = { onShare(dgpsStation) },
         onZoom,
         onCopyLocation
      )
   }
}

@Composable
private fun DgpsStationContent(
   dgpsStation: DgpsStation,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "${dgpsStation.featureNumber} ${dgpsStation.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      dgpsStation.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         dgpsStation.remarks?.let { remarks ->
            Text(
               text = remarks,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 8.dp)
            )
         }
      }

      DgpsStationFooter(
         dgpsStation = dgpsStation,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun DgpsStationFooter(
   dgpsStation: DgpsStation,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      DgpsStationLocation(dgpsStation.dms, onCopyLocation)
      DgpsStationActions(onShare, onZoom)
   }
}

@Composable
private fun DgpsStationLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun DgpsStationActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share DGPS Station"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to DGPS Station"
         )
      }
   }
}