package mil.nga.msi.ui.asam.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamAction
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsamsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onTap: (String) -> Unit,
   onAction: (AsamAction) -> Unit,
   viewModel: AsamsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val filters by viewModel.asamFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = AsamRoute.List.title,
         navigationIcon = Icons.Default.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort ASAMs")
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
                     contentDescription = "Filter ASAMs"
                  )
               }
            }
         }
      )

      Asams(
         pagingState = viewModel.asams,
         onTap = onTap,
         onCopyLocation = { onAction(AsamAction.Location(it)) },
         onZoom = { onAction(AsamAction.Zoom(it)) },
         onShare = { reference ->
            scope.launch {
               viewModel.getAsam(reference)?.let { asam ->
                  onAction(AsamAction.Share(asam.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun Asams(
   pagingState: Flow<PagingData<AsamListItem>>,
   onTap: (String) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (String) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val lazyItems = pagingState.collectAsLazyPagingItems()

   Surface(Modifier.fillMaxSize()) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(lazyItems) { item ->
            when (item) {
               is AsamListItem.AsamItem -> {
                  AsamCard(
                     asam = item.asam,
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.asam.latitude, item.asam.longitude)) },
                     onShare = { onShare(item.asam.reference) }
                  )
               }
               is AsamListItem.HeaderItem -> {
                  Text(
                     text = item.header,
                     fontWeight = FontWeight.Medium,
                     style = MaterialTheme.typography.labelSmall,
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
private fun AsamCard(
   asam: Asam,
   onTap: (String) -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(asam.reference) }
   ) {
      AsamContent(asam, onShare, onZoom, onCopyLocation)
   }
}

@Composable
private fun AsamContent(
   asam: Asam,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         asam.date.let { date ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Text(
               text = dateFormat.format(date),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }
      }

      val header = listOfNotNull(asam.hostility, asam.victim).joinToString(": ")
      Text(
         text = header,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         asam.description?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      AsamFooter(
         asam = asam,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun AsamFooter(
   asam: Asam,
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
      AsamLocation(asam.dms, onCopyLocation)
      AsamActions(onShare, onZoom)
   }
}

@Composable
private fun AsamLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun AsamActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}