package mil.nga.msi.ui.noticetomariners

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun NoticeToMarinersAllScreen(
   close: () -> Unit
) {
   Column(modifier = Modifier) {
      TopBar(
         title = NoticeToMarinersRoute.All.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      NoticeToMariners()
   }
}

@Composable
private fun NoticeToMariners(
) {
   Surface(
      color = MaterialTheme.colors.screenBackground
   ) {

   }
}