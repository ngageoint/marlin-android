package mil.nga.msi.ui.modu.list

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
import mil.nga.msi.datasource.modu.ModuListItem
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.modu.ModuAction
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModusScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   onTap: (String) -> Unit,
   onAction: (ModuAction) -> Unit,
   viewModel: ModusViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = ModuRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            IconButton(onClick = { openFilter() } ) {
               Icon(Icons.Default.FilterList, contentDescription = "Filter MODUs")
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
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxHeight()
   ) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(lazyItems) { item ->
            ModuCard(
               item = item,
               onTap = onTap,
               onZoom = { item?.let { onZoom(Point(it.latitude, it.longitude)) } },
               onShare = { item?.name?.let { onShare(it) } },
               onCopyLocation = onCopyLocation
            )
         }
      }
   }
}

@Composable
private fun ModuCard(
   item: ModuListItem?,
   onTap: (String) -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(item.name) }
      ) {
         ModuContent(item, onZoom, onShare, onCopyLocation)
      }
   }
}

@Composable
private fun ModuContent(
   item: ModuListItem,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.date.let { date ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            Text(
               text = dateFormat.format(date),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.overline,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }
      }

      Text(
         text = item.name,
         style = MaterialTheme.typography.h6,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.rigStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )
         }

         item.specialStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2
            )
         }
      }
      
      ModuFooter(item, onZoom, onShare, onCopyLocation)
   }
}

@Composable
private fun ModuFooter(
   item: ModuListItem,
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
      ModuLocation(item.dms, onCopyLocation)
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
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share MODU"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to MODU"
         )
      }
   }
}