package mil.nga.msi.ui.dgpsstation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.dgpsstation.DgpsStationListItem
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.ui.dgpsstation.DgpsStationAction
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun DgpsStationsScreen(
   openDrawer: () -> Unit,
   onTap: (DgpsStationKey) -> Unit,
   onAction: (DgpsStationAction) -> Unit,
   viewModel: DgpsStationsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = DgpsStationRoute.List.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      DgpsStations(
         pagingState = viewModel.radioBeacons,
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
   pagingState: Flow<PagingData<DgpsStationItem>>,
   onTap: (DgpsStationListItem) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (DgpsStationListItem) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val lazyItems = pagingState.collectAsLazyPagingItems()

   Surface(
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {

      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {

         items(lazyItems) { item ->
            when (item) {
               is DgpsStationItem.Header -> {
                  Text(
                     text = item.header,
                     fontWeight = FontWeight.Medium,
                     style = MaterialTheme.typography.caption,
                     modifier = Modifier.padding(vertical = 8.dp)
                  )
               }
               is DgpsStationItem.DgpsStation -> {
                  DgpsStationCard(
                     item = item.dgpsStation,
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
   item: DgpsStationListItem?,
   onTap: (DgpsStationListItem) -> Unit,
   onShare: (DgpsStationListItem) -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(item) }
      ) {
         DgpsStationContent(
            item,
            onShare = { onShare(item) },
            onZoom,
            onCopyLocation
         )
      }
   }
}

@Composable
private fun DgpsStationContent(
   item: DgpsStationListItem,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "${item.featureNumber} ${item.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.overline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      item.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.stationRemark?.let { stationRemark ->
            Text(
               text = stationRemark,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 8.dp)
            )
         }
      }

      DgpsStationFooter(
         item = item,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun DgpsStationFooter(
   item: DgpsStationListItem,
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
      DgpsStationLocation(item.dms, onCopyLocation)
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
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Radio Beacon"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Radio Beacon"
         )
      }
   }
}