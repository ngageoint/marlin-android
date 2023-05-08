package mil.nga.msi.ui.radiobeacon.list

import androidx.compose.foundation.background
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
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.radiobeacon.RadioBeaconAction
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioBeaconsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onTap: (RadioBeaconKey) -> Unit,
   onAction: (RadioBeaconAction) -> Unit,
   viewModel: RadioBeaconsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
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

      RadioBeacons(
         pagingState = viewModel.radioBeacons,
         onTap = { onTap(RadioBeaconKey.fromRadioBeacon(it)) },
         onCopyLocation = { onAction(RadioBeaconAction.Location(it)) },
         onZoom = { onAction(RadioBeaconAction.Zoom(it)) },
         onShare = { radioBeacon ->
            scope.launch {
               viewModel.getRadioBeacon(radioBeacon.volumeNumber, radioBeacon.featureNumber)?.let {
                  onAction(RadioBeaconAction.Share(it.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun RadioBeacons(
   pagingState: Flow<PagingData<RadioBeaconListItem>>,
   onTap: (RadioBeacon) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (RadioBeacon) -> Unit,
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
                  is RadioBeaconListItem.RadioBeaconItem -> it.radioBeacon.id
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
            when (val item = lazyItems[index]) {
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
                     beacon = item.radioBeacon,
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.radioBeacon.latitude, item.radioBeacon.longitude)) },
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
private fun RadioBeaconCard(
   beacon: RadioBeacon,
   onTap: (RadioBeacon) -> Unit,
   onShare: (RadioBeacon) -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(beacon) }
   ) {
      RadioBeaconContent(
         beacon,
         onShare = { onShare(beacon) },
         onZoom,
         onCopyLocation
      )
   }
}

@Composable
private fun RadioBeaconContent(
   beacon: RadioBeacon,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "${beacon.featureNumber} ${beacon.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      beacon.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      beacon.morseCode()?.let { code ->
         Text(
            text = beacon.morseLetter(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 4.dp)
         )

         MorseCode(text = code, modifier = Modifier.padding(top = 4.dp))
      }

      beacon.expandedCharacteristicWithoutCode()?.let {
         Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 0.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         beacon.stationRemark?.let { stationRemark ->
            Text(
               text = stationRemark,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 8.dp)
            )
         }
      }

      RadioBeaconFooter(
         beacon = beacon,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun MorseCode(
   text: String,
   modifier: Modifier = Modifier,
) {
   Row(modifier = modifier) {
      text.split(" ").forEach { letter ->
         if (letter == "-" || letter == "•") {
            Box(
               modifier = Modifier
                  .padding(end = 8.dp)
                  .height(5.dp)
                  .width(if (letter == "-") 24.dp else 8.dp)
                  .background(MaterialTheme.colorScheme.onSurface)
            )
         }
      }
   }
}

@Composable
private fun RadioBeaconFooter(
   beacon: RadioBeacon,
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
      RadioBeaconLocation(beacon.dms, onCopyLocation)
      RadioBeaconActions(onShare, onZoom)
   }
}

@Composable
private fun RadioBeaconLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun RadioBeaconActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share Radio Beacon"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to Radio Beacon"
         )
      }
   }
}