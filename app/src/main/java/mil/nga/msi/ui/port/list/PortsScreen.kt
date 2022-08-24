package mil.nga.msi.ui.port.list

import android.location.Location
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
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.port.PortListItem
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.location.generalDirection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.port.PortAction
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun PortsScreen(
   openDrawer: () -> Unit,
   onTap: (Int) -> Unit,
   onAction: (PortAction) -> Unit,
   viewModel: PortsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val location by viewModel.locationProvider.observeAsState()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = PortRoute.List.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      Ports(
         pagingState = viewModel.ports,
         location = location,
         onTap = onTap,
         onCopyLocation = { onAction(PortAction.Location(it)) },
         onZoom = { onAction(PortAction.Zoom(it)) },
         onShare = { portNumber ->
            scope.launch {
               viewModel.getPort(portNumber)?.let { port ->
                  onAction(PortAction.Share(port.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun Ports(
   pagingState: Flow<PagingData<PortListItem>>,
   location: Location?,
   onTap: (Int) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (Int) -> Unit,
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
            PortCard(
               item = item,
               location = location,
               onTap = onTap,
               onCopyLocation = { onCopyLocation(it) },
               onZoom = { item?.let { onZoom(Point(it.latitude, it.longitude)) }  },
               onShare = { item?.portNumber?.let { onShare(it) } }
            )
         }
      }
   }
}

@Composable
private fun PortCard(
   item: PortListItem?,
   location: Location?,
   onTap: (Int) -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(item.portNumber) }
      ) {
         PortContent(item, location, onShare, onZoom, onCopyLocation)
      }
   }
}

@Composable
private fun PortContent(
   item: PortListItem,
   location: Location?,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column {
      Row(
         Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
      ) {
         Column(
            Modifier.weight(1f)
         ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
               Text(
                  text = item.portName,
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.h6,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
               )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
               item.alternateName?.let {
                  Text(
                     text = it,
                     style = MaterialTheme.typography.body2,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }

         location?.let { location ->
            Row {
               val portLocation = Location("port").apply {
                  latitude = item.latitude
                  longitude = item.longitude
               }

               val distance = location.distanceTo(portLocation) / 1000
               val direction = location.generalDirection(portLocation)
               val nmi = distance * 0.539957
               CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                  Text(
                     text = "${String.format("%.2f", nmi)}, $direction",
                     style = MaterialTheme.typography.body2,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }
      }

      PortFooter(
         item = item,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun PortFooter(
   item: PortListItem,
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
      PortLocation(item.dms, onCopyLocation)
      PortActions(onShare, onZoom)
   }
}

@Composable
private fun PortLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun PortActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Port"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Port"
         )
      }
   }
}