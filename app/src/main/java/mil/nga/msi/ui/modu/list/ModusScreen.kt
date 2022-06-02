package mil.nga.msi.ui.modu.list

import androidx.compose.foundation.clickable
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
import mil.nga.msi.datasource.modu.ModuListItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ModusScreen(
   openDrawer: () -> Unit,
   onModuClick: (String) -> Unit,
   viewModel: ModusViewModel = hiltViewModel()
) {
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = "MODUs",
         buttonIcon = Icons.Filled.Menu,
         onButtonClicked = { openDrawer() }
      )
      Modus(viewModel.modus, onModuClick)
   }
}

@Composable
private fun Modus(
   pagingState: Flow<PagingData<ModuListItem>>,
   onModuClick: (String) -> Unit
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
               ModuCard(item, onModuClick)
            }
         }
      }
   }
}

@Composable
private fun ModuCard(
   item: ModuListItem?,
   onModuClick: (String) -> Unit
) {
   if (item != null) {
      Card(
         Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onModuClick(item.name) }
      ) {
         ModuContent(item)
      }
   }
}

@Composable
private fun ModuContent(item: ModuListItem) {
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

      Text(
         text = item.name,
         style = MaterialTheme.typography.h6,
         maxLines = 1,
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(top = 16.dp)
      )

      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         item.rigStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2,
               modifier = Modifier.padding(top = 4.dp)
            )
         }

         item.specialStatus?.let {
            Text(
               text = it,
               style = MaterialTheme.typography.body2
            )
         }
      }
      
      ModuFooter(item)
   }
}

@Composable
private fun ModuFooter(item: ModuListItem) {
   Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
         .fillMaxWidth()
         .padding(top = 8.dp)
   ) {
      ModuLocation(item)
      ModuActions()
   }
}


@Composable
private fun ModuLocation(item: ModuListItem) {
   TextButton(onClick = { /*TODO*/ }) {
      Text(text = item.dms.format())
   }
}

@Composable
private fun ModuActions() {
   Row {
      IconButton(onClick = {  }) {
         Icon(Icons.Default.Share,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Share MODU"
         )
      }
      IconButton(onClick = {  }) {
         Icon(Icons.Default.GpsFixed,
            tint = MaterialTheme.colors.primary,
            contentDescription = "Zoom to MODU"
         )
      }
   }
}