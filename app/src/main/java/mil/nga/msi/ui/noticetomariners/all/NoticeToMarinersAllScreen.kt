package mil.nga.msi.ui.noticetomariners.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.onSurfaceDisabled
import mil.nga.msi.ui.theme.screenBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoticeToMarinersAllScreen(
   onTap: (Int) -> Unit,
   close: () -> Unit,
   viewModel: NoticeToMarinersAllViewModel = hiltViewModel()
) {
   val notices by viewModel.noticeToMariners.observeAsState(emptyMap())

   Column {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Surface(Modifier.fillMaxSize()) {
         NoticeToMarinersItems(notices) {
            onTap(it)
         }
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoticeToMarinersItems(
   notices: Map<String, List<Int>>,
   onTap: (Int) -> Unit,
) {
   LazyColumn {
      notices.forEach { (year, notices) ->
         stickyHeader {
            NoticeHeader(year = year)
         }

         items(notices) { item ->
            NoticeToMarinersItem(
               noticeNumber = item,
               onTap = { onTap(item) }
            )
         }
      }
   }
}

@Composable
private fun NoticeHeader(
   year: String
) {
   Surface {
      Box(
         modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.screenBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp)
      ) {

         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceDisabled) {
            Text(
               text = year,
               fontWeight = FontWeight.Medium,
               style = MaterialTheme.typography.bodySmall
            )
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoticeToMarinersItem(
   noticeNumber: Int,
   onTap: (Int) -> Unit,
) {
   val calendar = Calendar.getInstance()
   calendar.set(Calendar.YEAR, noticeNumber.toString().take(4).toInt())
   calendar.set(Calendar.WEEK_OF_YEAR, noticeNumber.toString().takeLast(2).toInt())
   while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
      calendar.add(Calendar.DAY_OF_WEEK, 1)
   }

   val start = SimpleDateFormat("MMMM d",Locale.getDefault()).format(calendar.time)
   calendar.add(Calendar.DAY_OF_WEEK, 6)
   val end = SimpleDateFormat("MMMM d",Locale.getDefault()).format(calendar.time)

   ListItem(
      headlineContent = {
         Text(
            text = noticeNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
         )
      },
      supportingContent = {
         CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
               text = "$start - $end",
               style = MaterialTheme.typography.titleSmall
            )
         }
      },
      modifier = Modifier
         .fillMaxWidth()
         .clickable { onTap(noticeNumber) }
   )

   Divider(Modifier.padding(start = 16.dp))
}