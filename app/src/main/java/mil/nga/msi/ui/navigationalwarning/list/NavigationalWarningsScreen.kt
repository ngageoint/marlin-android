package mil.nga.msi.ui.navigationalwarning.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningAction
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NavigationalWarningsScreen(
   navigationArea: NavigationArea,
   close: () -> Unit,
   onTap: (NavigationalWarningKey) -> Unit,
   onAction: (NavigationalWarningAction) -> Unit,
   viewModel: NavigationalWarningsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   viewModel.setNavigationArea(navigationArea)
   val lastViewed by viewModel.getLastViewedWarning().observeAsState()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = NavigationWarningRoute.List.title,
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      val items by viewModel.navigationalWarningsByArea.observeAsState(emptyList())
      if (items.isNotEmpty()) {
         NavigationalWarnings(
            items = items,
            lastViewed = lastViewed,
            onTap = { onTap(it) },
            onShare = { key ->
               scope.launch {
                  viewModel.getNavigationalWarning(key)?.let { warning ->
                     onAction(NavigationalWarningAction.Share(warning.toString()))
                  }
               }
            },
            onItemViewed = { viewModel.setNavigationalWarningViewed(navigationArea, it) }
         )
      }
   }
}

@Composable
private fun NavigationalWarnings(
   items: List<NavigationalWarningListItem>,
   lastViewed: NavigationalWarning?,
   onTap: (NavigationalWarningKey) -> Unit,
   onShare: (NavigationalWarningKey) -> Unit,
   onItemViewed: (NavigationalWarningListItem) -> Unit
) {
   val contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
   val index = items.indexOfFirst {
      it.number == lastViewed?.number && it.year == lastViewed.year
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

   Surface(
      color = MaterialTheme.colors.screenBackground
   ) {
      Box {
         LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
         ) {
            itemsIndexed(
               items = items,
               key = { _, item ->
                  NavigationalWarningKey.fromNavigationWarning(item)
               }
            ) { i, item ->
               NavigationalWarningCard(
                  state = listState,
                  item = item,
                  onTap = onTap,
                  onShare = { onShare(it) },
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
               Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                     .height(40.dp)
                     .clip(RoundedCornerShape(20.dp))
                     .background(MaterialTheme.colors.primary)
                     .clickable {
                        scope.launch {
                           listState.animateScrollToItem(0)
                        }
                     }
                     .padding(horizontal = 16.dp)
               ) {
                  Icon(Icons.Default.ArrowUpward,
                     contentDescription = "Scroll to new warnings",
                     tint = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary),
                     modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                        .width(16.dp)
                  )

                  Text(
                     text = "$index Unread ${if (index == 1) "Warning" else "Warnings"}",
                     style = MaterialTheme.typography.body2,
                     color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.primary)
                  )
               }
            }
         }
      }
   }
}

@Composable
private fun NavigationalWarningCard(
   state: LazyListState,
   item: NavigationalWarningListItem,
   onTap: (NavigationalWarningKey) -> Unit,
   onShare: (NavigationalWarningKey) -> Unit,
   onItemViewed: (NavigationalWarningListItem) -> Unit
) {
   val isItemWithKeyInView by remember {
      derivedStateOf {
         val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
         if (visibleItemsInfo.isEmpty()) {
            false
         } else {
            val firstItem = visibleItemsInfo.first()
            if (NavigationalWarningKey.fromNavigationWarning(item) == firstItem.key) {
               val offset = firstItem.offset - state.layoutInfo.viewportStartOffset
               offset >= 0
            } else {
               state.layoutInfo
                  .visibleItemsInfo
                  .any { it.key == NavigationalWarningKey.fromNavigationWarning(item) }
            }
         }
      }
   }

   if (isItemWithKeyInView) {
      LaunchedEffect(Unit) { onItemViewed(item) }
   }

   Card(
      Modifier
         .fillMaxWidth()
         .clickable { onTap(NavigationalWarningKey.fromNavigationWarning(item)) }
   ) {
      NavigationalWarningContent(
         item,
         onShare = { onShare(NavigationalWarningKey.fromNavigationWarning(item)) }
      )
   }
}

@Composable
private fun NavigationalWarningContent(
   item: NavigationalWarningListItem,
   onShare: () -> Unit
) {
   Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.issueDate.let { date ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            Text(
               text = dateFormat.format(date),
               fontWeight = FontWeight.SemiBold,
               style = MaterialTheme.typography.overline,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
            )
         }
      }

      val identifier = "${item.number}/${item.year}"
      val subregions = item.subregions?.joinToString(",")?.let { "($it)" }
      val header = listOfNotNull(item.navigationArea.title, identifier, subregions).joinToString(" ")
      Text(
         text = header,
         style = MaterialTheme.typography.h6,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.text?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2,
               maxLines = 8,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.padding(top = 4.dp)
            )
         }
      }

      NavigationalWarningFooter(onShare)
   }
}

@Composable
private fun NavigationalWarningFooter(
   onShare: () -> Unit
) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      NavigationalWarningActions(onShare)
   }
}

@Composable
private fun NavigationalWarningActions(
   onShare: () -> Unit
) {
   Row {
      IconButton(
         onClick = { onShare() }
      ) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share Navigational Warning"
         )
      }
   }
}