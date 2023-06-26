package mil.nga.msi.ui.asam.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.AsamAction
import mil.nga.msi.ui.asam.AsamFooter
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.asam.AsamSummary
import mil.nga.msi.ui.main.TopBar
import java.util.*

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
         onZoom = { onAction(AsamAction.Zoom(it)) },
         onBookmark = {
            if (it.bookmarked) {
               viewModel.removeBookmark(it)
            } else {
               onAction(AsamAction.Bookmark(BookmarkKey.fromAsam(it)))
            }
         },
         onCopyLocation = { onAction(AsamAction.Location(it)) },
         onShare = { reference ->
            scope.launch {
               viewModel.getAsam(reference)?.let { asam ->
                  onAction(AsamAction.Share(asam))
               }
            }
         }
      )
   }
}

@Composable
private fun Asams(
   pagingState: Flow<PagingData<AsamListItemState>>,
   onTap: (String) -> Unit,
   onZoom: (Asam) -> Unit,
   onShare: (String) -> Unit,
   onBookmark: (Asam) -> Unit,
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
                  is AsamListItemState.AsamItemState -> it.asam.reference
                  is AsamListItemState.HeaderItemState -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is AsamListItemState.AsamItemState -> "asam"
                  is AsamListItemState.HeaderItemState -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is AsamListItemState.AsamItemState -> {
                     AsamCard(
                        asam = item.asam,
                        onTap = onTap,
                        onCopyLocation = { onCopyLocation(it) },
                        onZoom = { onZoom(item.asam) },
                        onShare = { onShare(item.asam.reference) },
                        onBookmark = { onBookmark(item.asam) }
                     )
                  }
                  is AsamListItemState.HeaderItemState -> {
                     Text(
                        text = item.header,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                     )
                  }
               }
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
   onBookmark: () -> Unit,
   onCopyLocation: (String) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onTap(asam.reference) }
   ) {
      AsamSummary(asam)
      AsamFooter(
         asam = asam,
         onZoom = onZoom,
         onShare = onShare,
         onBookmark = onBookmark,
         onCopyLocation = onCopyLocation
      )
   }
}