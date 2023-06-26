package mil.nga.msi.ui.modu.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.action.ModuAction
import mil.nga.msi.ui.modu.ModuFooter
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.modu.ModuSummary
import mil.nga.msi.ui.navigation.NavPoint
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModusScreen(
   openDrawer: () -> Unit,
   openFilter: () -> Unit,
   openSort: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: ModusViewModel = hiltViewModel()
) {
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
                        containerColor = MaterialTheme.colorScheme.tertiary,
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
         onAction = onAction
      )
   }
}

@Composable
private fun Modus(
   pagingState: Flow<PagingData<ModuListItem>>,
   onAction: (Action) -> Unit
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
                  is ModuListItem.ModuItem -> it.modu.name
                  is ModuListItem.HeaderItem -> it.header
               }
            },
            contentType = lazyItems.itemContentType {
               when (it) {
                  is ModuListItem.ModuItem -> "modu"
                  is ModuListItem.HeaderItem -> "header"
               }
            }
         ) { index ->
            lazyItems[index]?.let { item ->
               when (item) {
                  is ModuListItem.ModuItem -> {
                     ModuCard(
                        modu = item.modu,
                        onAction = onAction
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
               }
            }
         }
      }
   }
}

@Composable
private fun ModuCard(
   modu: Modu,
   onAction: (Action) -> Unit
) {
   Card(
      Modifier
         .fillMaxWidth()
         .padding(bottom = 8.dp)
         .clickable { onAction(ModuAction.Tap(modu)) }
   ) {
      ModuSummary(modu = modu)
      ModuFooter(
         modu,
         onZoom = { onAction(Action.Zoom(NavPoint(modu.latitude, modu.longitude))) },
         onShare = { onAction(ModuAction.Share(modu)) },
         onBookmark = { onAction(Action.Bookmark(BookmarkKey.fromModu(modu))) },
         onCopyLocation = { onAction(ModuAction.Location(it)) }
      )
   }
}