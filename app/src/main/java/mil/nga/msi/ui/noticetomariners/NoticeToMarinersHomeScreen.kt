package mil.nga.msi.ui.noticetomariners

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar

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
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.fillMaxSize()
   ) {
      Column(Modifier.padding(top = 32.dp)) {
         Surface {
            Box(Modifier
               .fillMaxWidth()
               .clickable { onTap(NoticeToMarinersHomeChoice.ALL) }
               .padding(16.dp)
            ) {
               Text(
                  text = "View All Notice to Mariners"
               )
            }
         }

         Divider(Modifier.fillMaxWidth())

         Surface() {
            Box(Modifier
               .fillMaxWidth()
               .clickable { onTap(NoticeToMarinersHomeChoice.QUERY) }
               .padding(16.dp)
            ) {
               Text(
                  text = "Chart Corrections",
               )
            }
         }
      }
   }
}