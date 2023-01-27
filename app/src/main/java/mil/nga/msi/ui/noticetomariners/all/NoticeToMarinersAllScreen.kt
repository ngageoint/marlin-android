package mil.nga.msi.ui.noticetomariners.all

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.noticetomariners.NoticeToMarinersRoute
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun NoticeToMarinersAllScreen(
   onTap: (Int) -> Unit,
   close: () -> Unit,
   viewModel: NoticeToMarinersAllViewModel = hiltViewModel()
) {
   val notices by viewModel.noticeToMariners.observeAsState(emptyList())

   Column(modifier = Modifier) {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      NoticeToMarinersItems(notices) {
         onTap(it)
      }
   }
}

@Composable
private fun NoticeToMarinersItems(
   notices: List<Int>,
   onTap: (Int) -> Unit,
) {
   Surface(
      color = MaterialTheme.colors.screenBackground
   ) {
      LazyColumn(
         modifier = Modifier.padding(horizontal = 8.dp),
         contentPadding = PaddingValues(top = 16.dp)
      ) {
         items(notices) { item ->
            NoticeToMarinersItem(
               noticeToMariners = item,
               onTap = { onTap(it) }
            )
         }
      }
   }
}

@Composable
private fun NoticeToMarinersItem(
   noticeToMariners: Int?,
   onTap: (Int) -> Unit,
) {
   noticeToMariners?.let {
      Column {
         Row(
            Modifier
               .fillMaxWidth()
               .clickable {
                  onTap(it)
               }
               .padding(16.dp)
         ) {
            Text(text = "Notice: ${it.toString().take(4).takeLast(2)}/${it.toString().takeLast(2).toInt()}")
         }
         Divider()
      }
   }
}