package mil.nga.msi.ui.light.list

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
import mil.nga.msi.datasource.light.Light
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
   openSort: () -> Unit,
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
            IconButton(onClick = { openSort() } ) {
               Icon(Icons.Default.SwapVert, contentDescription = "Sort Lights")
            }

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
                        .background(MaterialTheme.colorScheme.secondary)
                        .align(Alignment.TopEnd)
                  ) {
                     Text(
                        text = "${filters.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
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
   pagingState: Flow<PagingData<LightListItem>>,
   onTap: (Light) -> Unit,
   onZoom: (Point) -> Unit,
   onShare: (Light) -> Unit,
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
               is LightListItem.HeaderItem -> {
                  Text(
                     text = item.header,
                     fontWeight = FontWeight.Medium,
                     style = MaterialTheme.typography.bodySmall,
                     modifier = Modifier.padding(vertical = 8.dp)
                  )
               }
               is LightListItem.LightItem -> {
                  LightCard(
                     light = item.light,
                     onTap = onTap,
                     onCopyLocation = { onCopyLocation(it) },
                     onZoom = { onZoom(Point(item.light.latitude, item.light.longitude)) },
                     onShare = onShare
                  )
               }
               else -> { /* TODO item is null */}
            }
         }
      }
   }
}

@Composable
private fun LightCard(
   light: Light,
   onTap: (Light) -> Unit,
   onShare: (Light) -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(light) }
   ) {
      LightContent(
         light,
         onShare = { onShare(light) },
         onZoom,
         onCopyLocation
      )
   }
}

@Composable
private fun LightContent(
   light: Light,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         Text(
            text = "${light.featureNumber} ${light.internationalFeature ?: ""} ${light.volumeNumber}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
         )
      }

      light.name?.let { name ->
         Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
         )
      }

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         light.structure?.let { structure ->
            Text(
               text = structure,
               style = MaterialTheme.typography.bodyMedium,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      LightFooter(
         light = light,
         onShare = onShare,
         onZoom = onZoom,
         onCopyLocation = onCopyLocation
      )
   }
}

@Composable
private fun LightFooter(
   light: Light,
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
      LightLocation(light.dms, onCopyLocation)
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
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Share Light"
         )
      }
      IconButton(onClick = { onZoom() }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colorScheme.tertiary,
            contentDescription = "Zoom to Light"
         )
      }
   }
}