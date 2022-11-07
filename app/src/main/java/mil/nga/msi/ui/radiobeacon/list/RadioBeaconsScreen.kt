package mil.nga.msi.ui.radiobeacon.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import mil.nga.msi.datasource.radiobeacon.RadioBeaconListItem
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.radiobeacon.RadioBeaconAction
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun RadioBeaconsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   onTap: (RadioBeaconKey) -> Unit,
   onAction: (RadioBeaconAction) -> Unit,
   viewModel: RadioBeaconsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = RadioBeaconRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openFilter() } ) {
               Icon(Icons.Default.FilterList, contentDescription = "Filter Radio Beacons")
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
   pagingState: Flow<PagingData<RadioBeaconItem>>,
   onTap: (RadioBeaconListItem) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (RadioBeaconListItem) -> Unit,
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
               is RadioBeaconItem.Header -> {
                  Text(
                     text = item.header,
                     fontWeight = FontWeight.Medium,
                     style = MaterialTheme.typography.caption,
                     modifier = Modifier.padding(vertical = 8.dp)
                  )
               }
               is RadioBeaconItem.RadioBeacon -> {
                  RadioBeaconCard(
                     item = item.radioBeacon,
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
   item: RadioBeaconListItem?,
   onTap: (RadioBeaconListItem) -> Unit,
   onShare: (RadioBeaconListItem) -> Unit,
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
         RadioBeaconContent(
            item,
            onShare = { onShare(item) },
            onZoom,
            onCopyLocation
         )
      }
   }
}

@Composable
private fun RadioBeaconContent(
   item: RadioBeaconListItem,
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

      item.morseCode()?.let { code ->
         Text(
            text = item.morseLetter(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 4.dp)
         )

         MorseCode(text = code, modifier = Modifier.padding(top = 4.dp))
      }

      item.expandedCharacteristicWithoutCode()?.let {
         Text(
            text = it,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(top = 0.dp)
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

      RadioBeaconFooter(
         item = item,
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
                  .background(MaterialTheme.colors.onSurface)
            )
         }
      }
   }
}

@Composable
private fun RadioBeaconFooter(
   item: RadioBeaconListItem,
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
      RadioBeaconLocation(item.dms, onCopyLocation)
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