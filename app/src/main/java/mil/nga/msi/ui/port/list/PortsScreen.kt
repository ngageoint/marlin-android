package mil.nga.msi.ui.port.list

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.location.generalDirection
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.port.PortAction
import mil.nga.msi.ui.port.PortRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onTap: (Int) -> Unit,
   onAction: (PortAction) -> Unit,
   viewModel: PortsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
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
                        containerColor = MaterialTheme.colorScheme.secondary,
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
      modifier = Modifier.fillMaxHeight()
   ) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(lazyItems) { item ->
            when (item) {
               is PortListItem.PortItem -> {
                  PortCard(
                     port = item.port,
                     location = location,
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.port.latitude, item.port.longitude)) },
                     onShare = { onShare(item.port.portNumber) }
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
               else -> { /* TODO item is null */}
            }
         }
      }
   }
}

@Composable
private fun PortCard(
   port: Port,
   location: Location?,
   onTap: (Int) -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(port.portNumber) }
   ) {
      PortContent(port, location, onShare, onZoom, onCopyLocation)
   }
}

@Composable
private fun PortContent(
   port: Port,
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
            Text(
               text = port.portName,
               style = MaterialTheme.typography.titleLarge,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               port.alternateName?.let {
                  Text(
                     text = it,
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }

         location?.let { location ->
            Row {
               val portLocation = Location("port").apply {
                  latitude = port.latitude
                  longitude = port.longitude
               }

               val distance = location.distanceTo(portLocation) / 1000
               val direction = location.generalDirection(portLocation)
               val nmi = distance * 0.539957
               CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                  Text(
                     text = "${String.format("%.2f", nmi)}, $direction",
                     style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.padding(top = 4.dp)
                  )
               }
            }
         }
      }

      PortFooter(
         port = port,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun PortFooter(
   port: Port,
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
      PortLocation(port.dms, onCopyLocation)
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
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Share Port"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Zoom to Port"
         )
      }
   }
}