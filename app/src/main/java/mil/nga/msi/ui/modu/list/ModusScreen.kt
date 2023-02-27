package mil.nga.msi.ui.modu.list

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
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuAction
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModusScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onTap: (String) -> Unit,
   onAction: (ModuAction) -> Unit,
   viewModel: ModusViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val filters by viewModel.moduFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = ModuRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort MODUs")
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
                     contentDescription = "Filter MODUs"
                  )
               }
            }
         }
      )
      Modus(
         viewModel.modus,
         onTap = onTap,
         onCopyLocation = { onAction(ModuAction.Location(it)) },
         onZoom = { onAction( ModuAction.Zoom(it)) },
         onShare = { name ->
            scope.launch {
               viewModel.getModu(name)?.let { modu ->
                  onAction(ModuAction.Share(modu.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun Modus(
   pagingState: Flow<PagingData<ModuListItem>>,
   onTap: (String) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (String) -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val lazyItems = pagingState.collectAsLazyPagingItems()
   Surface(
      color = MaterialTheme.colorScheme.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(lazyItems) { item ->
            when (item) {
               is ModuListItem.ModuItem -> {
                  ModuCard(
                     modu = item.modu,
                     onTap = onTap,
                     onZoom = { onZoom(Point(item.modu.latitude, item.modu.longitude)) },
                     onShare = { item.modu.name.let { onShare(it) } },
                     onCopyLocation = onCopyLocation
                  )
               }
               is ModuListItem.HeaderItem -> {
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
private fun ModuCard(
   modu: Modu?,
   onTap: (String) -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (modu != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(modu.name) }
      ) {
         ModuContent(modu, onZoom, onShare, onCopyLocation)
      }
   }
}

@Composable
private fun ModuContent(
   modu: Modu,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         modu.date.let { date ->
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

      Text(
         text = modu.name,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         modu.rigStatus?.let {
            Text(
               text = it.name,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 4.dp)
            )
         }

         modu.specialStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium
            )
         }
      }
      
      ModuFooter(modu, onZoom, onShare, onCopyLocation)
   }
}

@Composable
private fun ModuFooter(
   modu: Modu,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      ModuLocation(modu.dms, onCopyLocation)
      ModuActions(onZoom, onShare)
   }
}


@Composable
private fun ModuLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun ModuActions(
   onZoom: () -> Unit,
   onShare: () -> Unit
) {
   Row {
      IconButton(onClick = { onShare() }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Share MODU"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Zoom to MODU"
         )
      }
   }
}