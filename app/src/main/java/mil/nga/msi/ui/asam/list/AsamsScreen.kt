package mil.nga.msi.ui.asam.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.ui.theme.MsiTheme
import androidx.paging.compose.items
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.DMS
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.ui.asam.AsamAction
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsamsScreen(
   openDrawer: () -> Unit,
   onTap: (String) -> Unit,
   onAction: (AsamAction) -> Unit,
   viewModel: AsamsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = AsamRoute.List.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
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
   MsiTheme {
      Surface(
         color = Color(0x19000000),
         modifier = Modifier.fillMaxHeight()
      ) {
         LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(top = 16.dp)
         ) {
            items(lazyItems) { item ->
               AsamCard(
                  item = item,
                  onTap = onTap,
                  onCopyLocation = { onCopyLocation(it) },
                  onZoom = { item?.let { onZoom(Point(it.latitude, it.longitude)) }  },
                  onShare = { item?.reference?.let { onShare(it) } }
               )
            }
         }
      }
   }
}

@Composable
private fun AsamCard(
   item: AsamListItem?,
   onTap: (String) -> Unit,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(item.reference) }
      ) {
         AsamContent(item, onShare, onZoom, onCopyLocation)
      }
   }
}

@Composable
private fun AsamContent(
   item: AsamListItem,
   onShare: () -> Unit,
   onZoom: () -> Unit,
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

      val header = listOfNotNull(item.hostility, item.victim).joinToString(": ")
      Text(
         text = header,
         style = MaterialTheme.typography.h6,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.description?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }
      
      AsamFooter(
         item = item,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun AsamFooter(
   item: AsamListItem,
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
      AsamLocation(item.dms, onCopyLocation)
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
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}