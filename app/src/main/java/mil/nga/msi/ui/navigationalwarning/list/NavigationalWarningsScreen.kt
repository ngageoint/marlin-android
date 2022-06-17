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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import androidx.paging.compose.items
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.navigationwarning.NavigationArea
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

   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = NavigationWarningRoute.List.title,
         buttonIcon = Icons.Default.ArrowBack,
         onButtonClicked = { close() }
      )

      NavigationalWarnings(
         pagingState = viewModel.navigationalWarningsByArea,
         onTap = { onTap(it) },
         onShare = { key ->
            scope.launch {
               viewModel.getNavigationalWarning(key)?.let { warning ->
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
   onTap: (NavigationalWarningKey) -> Unit,
   onShare: (NavigationalWarningKey) -> Unit
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
            NavigationalWarningCard(
               item = item,
               onTap = onTap,
               onShare = { onShare(it) }
            )
         }
      }
   }
}

@Composable
private fun NavigationalWarningCard(
   item: NavigationalWarningListItem?,
   onTap: (NavigationalWarningKey) -> Unit,
   onShare: (NavigationalWarningKey) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onTap(NavigationalWarningKey.fromNavigationWarning(item)) }
      ) {
         NavigationalWarningContent(
            item,
            onShare = { onShare(NavigationalWarningKey.fromNavigationWarning(item)) }
         )
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
            contentDescription = "Share Navigational Warning"
         )
      }
   }
}