package mil.nga.msi.ui.light.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import mil.nga.msi.datasource.light.LightListItem
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.ui.light.LightAction
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.location.LocationTextButton
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigation.Point
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun LightsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   onTap: (LightKey) -> Unit,
   onAction: (LightAction) -> Unit,
   viewModel: LightsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val filters by viewModel.lightFilters.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = LightRoute.List.title,
         navigationIcon = Icons.Filled.Menu,
         onNavigationClicked = { openDrawer() },
         actions = {
            Box {
               IconButton(onClick = { openFilter() } ) {
                  Icon(Icons.Default.FilterList, contentDescription = "Filter Lights")
               }

               if (filters.isNotEmpty()) {
                  Box(
                     contentAlignment = Alignment.Center,
                     modifier = Modifier
                        .clip(CircleShape)
                        .height(24.dp)
                        .background(MaterialTheme.colors.secondary)
                        .align(Alignment.TopEnd)
                  ) {
                     Text(
                        text = "${filters.size}",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colors.onPrimary
                     )
                  }
               }
            }
         }
      )

      Lights(
         pagingState = viewModel.lights,
         onTap = { onTap(LightKey.fromLight(it)) },
         onCopyLocation = { onAction(LightAction.Location(it)) },
         onZoom = { onAction(LightAction.Zoom(it)) },
         onShare = { light ->
            scope.launch {
               viewModel.getLight(light.volumeNumber, light.featureNumber, light.characteristicNumber)?.let {
                  onAction(LightAction.Share(it.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun Lights(
   pagingState: Flow<PagingData<LightItem>>,
   onTap: (LightListItem) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (LightListItem) -> Unit,
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
               is LightItem.Header -> {
                  Text(
                     text = item.header,
                     fontWeight = FontWeight.Medium,
                     style = MaterialTheme.typography.caption,
                     modifier = Modifier.padding(vertical = 8.dp)
                  )
               }
               is LightItem.Light -> {
                  LightCard(
                     item = item.light,
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.light.latitude, item.light.longitude)) },
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
private fun LightCard(
   item: LightListItem?,
   onTap: (LightListItem) -> Unit,
   onShare: (LightListItem) -> Unit,
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
         LightContent(
            item,
            onShare = { onShare(item) },
            onZoom,
            onCopyLocation
         )
      }
   }
}

@Composable
private fun LightContent(
   item: LightListItem,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Text(
            text = "${item.featureNumber} ${item.internationalFeature ?: ""} ${item.volumeNumber}",
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
         item.structure?.let { structure ->
            Text(
               text = structure,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      LightFooter(
         item = item,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun LightFooter(
   item: LightListItem,
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
      LightLocation(item.dms, onCopyLocation)
      LightActions(onShare, onZoom)
   }
}

@Composable
private fun LightLocation(
   dms: DMS,
   onCopyLocation: (String) -> Unit
) {
   LocationTextButton(
      dms = dms,
      onCopiedToClipboard = { onCopyLocation(it) }
   )
}

@Composable
private fun LightActions(
   onShare: () -> Unit,
   onZoom: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Light"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to Light"
         )
      }
   }
}