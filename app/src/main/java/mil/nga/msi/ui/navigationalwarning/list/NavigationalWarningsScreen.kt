package mil.nga.msi.ui.navigationalwarning.list

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
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import mil.nga.msi.ui.navigationalwarning.NavigationalWarningAction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NavigationalWarningsScreen(
   openDrawer: () -> Unit,
   onTap: (Int) -> Unit,
   onAction: (NavigationalWarningAction) -> Unit,
   viewModel: NavigationalWarningsViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = NavigationWarningRoute.List.title,
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )

      NavigationalWarnings(
         pagingState = viewModel.navigationalWarnings,
         onTap = { onTap(it) },
         onShare = { number ->
            scope.launch {
               viewModel.getNavigationalWarning(number)?.let { warning ->
                  onAction(NavigationalWarningAction.Share(warning.toString()))
               }
            }
         }
      )
   }
}

@Composable
private fun NavigationalWarnings(
   pagingState: Flow<PagingData<NavigationalWarningListItem>>,
   onTap: (Int) -> Unit,
   onShare: (Int) -> Unit
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
               NavigationalWarningCard(
                  item = item,
                  onTap = onTap,
                  onShare = { item?.number?.let { onShare(it) } }
               )
            }
         }
      }
   }
}

@Composable
private fun NavigationalWarningCard(
   item: NavigationalWarningListItem?,
   onTap: (Int) -> Unit,
   onShare: () -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(item.number) }
      ) {
         NavigationalWarningContent(item, onShare)
      }
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
            contentDescription = "Share ASAM"
         )
      }
   }
}