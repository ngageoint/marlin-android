package mil.nga.msi.ui.asam.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.TopBar
import mil.nga.msi.ui.theme.MsiTheme
import androidx.paging.compose.items
import androidx.paging.compose.collectAsLazyPagingItems
import mil.nga.msi.datasource.asam.AsamListItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AsamsScreen(
   openDrawer: () -> Unit,
   viewModel: AsamsViewModel = hiltViewModel()
) {
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "ASAMs",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )
      Asams(viewModel.asams)
   }
}

@Composable
private fun Asams(pagingState: Flow<PagingData<AsamListItem>>) {
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
               AsamCard(item)
            }
         }
      }
   }
}

@Composable
private fun AsamCard(item: AsamListItem?) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
      ) {
         AsamContent(item)
      }
   }
}

@Composable
private fun AsamContent(item: AsamListItem) {
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
               modifier = Modifier.padding(top = 8.dp)
            )
         }
      }
      
      AsamFooter(item)
   }
}

@Composable
private fun AsamFooter(item: AsamListItem) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      AsamLocation(item)
      AsamActions()
   }
}

@Composable
private fun AsamActions() {
   Row {
      IconButton(onClick = {  }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share ASAM"
         )
      }
      IconButton(onClick = {  }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to ASAM"
         )
      }
   }
}

@Composable
private fun AsamLocation(item: AsamListItem) {
   val dms = "24 42 00 N, 13 36 57 W"
   TextButton(onClick = { /*TODO*/ }) {
      Text(text = dms)
   }
}