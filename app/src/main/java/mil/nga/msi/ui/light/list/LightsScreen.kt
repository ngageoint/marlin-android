package mil.nga.msi.ui.light.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.action.LightAction
import mil.nga.msi.ui.datasource.DataSourceFooter
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.light.LightSummary
import mil.nga.msi.ui.main.TopBar

@Composable
fun LightsScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: LightsViewModel = hiltViewModel()
) {
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
         onTap = { onAction(LightAction.Tap(it)) },
         onZoom = { onAction(LightAction.Zoom(it.latLng)) },
         onShare = { onAction(LightAction.Share(it)) },
         onBookmark = { (light, bookmark) ->
            if (bookmark == null) {
               onAction(Action.Bookmark(BookmarkKey.fromLight(light)))
            } else {
               viewModel.deleteBookmark(bookmark)
            }
         },
         onCopyLocation = { onAction(LightAction.Location(it)) }
      )
   }
}

@Composable
private fun Lights(
   pagingState: Flow<PagingData<LightListItem>>,
   onTap: (Light) -> Unit,
   onZoom: (Light) -> Unit,
   onShare: (Light) -> Unit,
   onBookmark: (LightWithBookmark) -> Unit,
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
                  is LightListItem.LightItem -> it.lightWithBookmark.light.id
                  is LightListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is LightListItem.LightItem -> "light"
                  is LightListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
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
                        lightWithBookmark = item.lightWithBookmark,
                        onTap = { onTap(item.lightWithBookmark.light) },
                        onZoom = { onZoom(item.lightWithBookmark.light) },
                        onShare = { onShare(item.lightWithBookmark.light) },
                        onBookmark = { onBookmark(item.lightWithBookmark) },
                        onCopyLocation = onCopyLocation
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun LightCard(
   lightWithBookmark: LightWithBookmark,
   onTap: () -> Unit,
   onZoom: () -> Unit,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap() }
   ) {
      LightContent(
         lightWithBookmark,
         onShare = onShare,
         onZoom,
         onBookmark,
         onCopyLocation
      )
   }
}

@Composable
private fun LightContent(
  lightWithBookmark: LightWithBookmark,
   onShare: () -> Unit,
   onZoom: () -> Unit,
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   val (light, bookmark) = lightWithBookmark

   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      LightSummary(lightWithBookmark = lightWithBookmark)

      DataSourceFooter(
         latLng = light.latLng,
         bookmarked = bookmark != null,
         onShare = onShare,
         onZoom = onZoom,
         onBookmark = onBookmark,
         onCopyLocation = onCopyLocation
      )
   }
}