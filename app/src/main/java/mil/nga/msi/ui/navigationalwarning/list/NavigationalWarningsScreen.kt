package mil.nga.msi.ui.navigationalwarning.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItemWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.action.Action
import mil.nga.msi.ui.datasource.DataSourceActions
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.action.NavigationalWarningAction
import mil.nga.msi.ui.bookmark.BookmarkNotes
import mil.nga.msi.ui.export.ExportDataSource
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NavigationalWarningsScreen(
   navigationArea: NavigationArea,
   close: () -> Unit,
   onAction: (Action) -> Unit,
   viewModel: NavigationalWarningsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   viewModel.setNavigationArea(navigationArea)
   val lastViewed by viewModel.getLastViewedWarning().observeAsState()
   val items by viewModel.navigationalWarningsByArea.observeAsState(emptyList())

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = NavigationWarningRoute.List.title,
         navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      if (items.isNotEmpty()) {
         Box(Modifier.fillMaxWidth()) {
            NavigationalWarnings(
               items = items,
               lastViewed = lastViewed,
               onTap = {
                  val key = NavigationalWarningKey.fromNavigationWarning(it)
                  onAction(NavigationalWarningAction.Tap(key))
               },
               onShare = { warning ->
                  scope.launch {
                     val key = NavigationalWarningKey.fromNavigationWarning(warning)
                     viewModel.getNavigationalWarning(key)?.let { warning ->
                        onAction(NavigationalWarningAction.Share(warning))
                     }
                  }
               },
               onZoom = { warning ->
                  scope.launch {
                     val key = NavigationalWarningKey.fromNavigationWarning(warning)
                     viewModel.getNavigationalWarning(key)?.let { warning ->
                        warning.bounds()?.let { bounds ->
                           onAction(NavigationalWarningAction.Zoom(bounds))
                        }
                     }
                  }
               },
               onBookmark = { (warning, bookmark) ->
                  if (bookmark == null) {
                     onAction(Action.Bookmark(BookmarkKey.fromNavigationalWarning(warning)))
                  } else {
                     viewModel.deleteBookmark(bookmark)
                  }
               },
               onItemViewed = { viewModel.setNavigationalWarningViewed(navigationArea, it) }
            )

            Box(
               Modifier
                  .align(Alignment.BottomEnd)
                  .padding(16.dp)
            ) {
               FloatingActionButton(
                  containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                  onClick = {
                     val export = ExportDataSource.NavigationalWarning(navigationArea = navigationArea)
                     onAction(Action.Export(listOf(export)))
                  }
               ) {
                  Icon(Icons.Outlined.Download,
                     contentDescription = "Export digital GPS stations as GeoPackage"
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun NavigationalWarnings(
   items: List<NavigationalWarningListItemWithBookmark>,
   lastViewed: NavigationalWarning?,
   onTap: (NavigationalWarningListItem) -> Unit,
   onShare: (NavigationalWarningListItem) -> Unit,
   onZoom: (NavigationalWarningListItem) -> Unit,
   onBookmark: (NavigationalWarningListItemWithBookmark) -> Unit,
   onItemViewed: (NavigationalWarningListItem) -> Unit
) {
   val contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
   val index = items.indexOfFirst { (warning, _) ->
      warning.number == lastViewed?.number && warning.year == lastViewed.year
   }.takeIf { index -> index >= 0 } ?: (items.size - 1)

   val scope = rememberCoroutineScope()
   val listState = rememberLazyListState(index, with(LocalDensity.current) { contentPadding.calculateTopPadding().roundToPx() })

   val firstItemNotVisible by remember {
      derivedStateOf {
         val layoutInfo = listState.layoutInfo
         val visibleItemsInfo = layoutInfo.visibleItemsInfo

         if (listState.firstVisibleItemIndex == 0) {
            visibleItemsInfo.firstOrNull()?.let {
               it.offset != 0
            } ?: false
         } else true
      }
   }

   Surface {
      Box {
         LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
         ) {
            itemsIndexed(
               items = items,
               key = { _, item ->
                  NavigationalWarningKey.fromNavigationWarning(item.navigationalWarning)
               }
            ) { i, item ->
               NavigationalWarningCard(
                  state = listState,
                  item = item,
                  onTap = onTap,
                  onShare = { onShare(it) },
                  onZoom = onZoom,
                  onBookmark = onBookmark,
                  onItemViewed = {
                     if (i < index) {
                        onItemViewed(it)
                     }
                  }
               )
            }
         }

         if (index != 0 && firstItemNotVisible) {
            Box(
               contentAlignment = Alignment.Center,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp)
            ) {
               Surface(
                  contentColor = MaterialTheme.colorScheme.onPrimary
               ) {
                  Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                           scope.launch {
                              listState.animateScrollToItem(0)
                           }
                        }
                        .padding(horizontal = 16.dp)
                  ) {
                     Icon(Icons.Default.ArrowUpward,
                        contentDescription = "Scroll to new warnings",
                        modifier = Modifier
                           .padding(end = 4.dp)
                           .height(16.dp)
                           .width(16.dp)
                     )

                     Text(
                        text = "$index Unread ${if (index == 1) "Warning" else "Warnings"}",
                        style = MaterialTheme.typography.bodyMedium,
                     )
                  }
               }
            }
         }
      }
   }
}

@Composable
private fun NavigationalWarningCard(
   state: LazyListState,
   item: NavigationalWarningListItemWithBookmark,
   onTap: (NavigationalWarningListItem) -> Unit,
   onShare: (NavigationalWarningListItem) -> Unit,
   onBookmark: (NavigationalWarningListItemWithBookmark) -> Unit,
   onItemViewed: (NavigationalWarningListItem) -> Unit,
   onZoom: ((NavigationalWarningListItem) -> Unit)? = null,
) {
   val warning = item.navigationalWarning

   val isItemWithKeyInView by remember {
      derivedStateOf {
         val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
         if (visibleItemsInfo.isEmpty()) {
            false
         } else {
            val firstItem = visibleItemsInfo.first()
            if (NavigationalWarningKey.fromNavigationWarning(warning) == firstItem.key) {
               val offset = firstItem.offset - state.layoutInfo.viewportStartOffset
               offset >= 0
            } else {
               state.layoutInfo
                  .visibleItemsInfo
                  .any { it.key == NavigationalWarningKey.fromNavigationWarning(warning) }
            }
         }
      }
   }

   if (isItemWithKeyInView) {
      LaunchedEffect(Unit) { onItemViewed(warning) }
   }

   Card(
      Modifier
         .fillMaxWidth()
         .clickable { onTap(warning) }
   ) {
      NavigationalWarningContent(
         item,
         onShare = { onShare(warning) },
         onBookmark = { onBookmark(item) },
         onZoom = if (warning.geoJson != null) {
            { onZoom?.invoke(warning) }
         } else null
      )
   }
}

@Composable
private fun NavigationalWarningContent(
   item: NavigationalWarningListItemWithBookmark,
   onShare: () -> Unit,
   onBookmark: () -> Unit,
   onZoom: (() -> Unit)? = null
) {
   val (warning, bookmark) = item

   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         warning.issueDate.let { date ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            Text(
               text = dateFormat.format(date),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.labelSmall,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }
      }

      val identifier = "${warning.number}/${warning.year}"
      val subregions = warning.subregions?.joinToString(",")?.let { "($it)" }
      val header = listOfNotNull(warning.navigationArea.title, identifier, subregions).joinToString(" ")
      Text(
         text = header,
         style = MaterialTheme.typography.titleLarge,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
         warning.text?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.bodyMedium,
               maxLines = 8,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      BookmarkNotes(
         notes = bookmark?.notes
      )

      DataSourceActions(
         bookmarked = bookmark != null,
         onShare = onShare,
         onZoom = onZoom,
         onBookmark = onBookmark
      )
   }
}

