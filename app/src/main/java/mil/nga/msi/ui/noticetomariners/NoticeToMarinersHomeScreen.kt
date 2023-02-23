package mil.nga.msi.ui.noticetomariners

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

enum class NoticeToMarinersHomeChoice {
   ALL,
   QUERY
}

@Composable
fun NoticeToMarinersHomeScreen(
   openDrawer: () -> Unit,
   onTap: (NoticeToMarinersHomeChoice) -> Unit
) {
   Column(modifier = Modifier) {
      TopBar(
         title = NoticeToMarinersRoute.Home.title,
         navigationIcon = Icons.Default.Menu,
         onNavigationClicked = { openDrawer() }
      )

      NoticeToMariners(onTap)
   }
}

@Composable
private fun NoticeToMariners(
   onTap: (NoticeToMarinersHomeChoice) -> Unit,
) {
   Surface(
      color = MaterialTheme.colors.screenBackground,
      modifier = Modifier.fillMaxSize()
   ) {
      Column(Modifier.padding(top = 32.dp)) {
         Box(Modifier
            .fillMaxWidth()
            .clickable { onTap(NoticeToMarinersHomeChoice.ALL) }
            .background(MaterialTheme.colors.surface)
            .padding(16.dp)
         ) {
            Text(
               text = "View All Notice to Mariners"
            )
         }

         Divider(Modifier.fillMaxWidth())

         Box(Modifier
            .fillMaxWidth()
            .clickable { onTap(NoticeToMarinersHomeChoice.QUERY) }
            .background(MaterialTheme.colors.surface)
            .padding(16.dp)
         ) {
            Text(
               text = "Chart Corrections",
            )
         }
      }
   }
}