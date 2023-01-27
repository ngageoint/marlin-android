package mil.nga.msi.ui.noticetomariners.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun NoticeToMarinersGraphicScreen(
   title: String,
   url: String,
   onShare: () -> Unit,
   close: () -> Unit
) {
   Column {
      TopBar(
         title = title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() },
         actions = {
            IconButton(onClick = { onShare() } ) {
               Icon(Icons.Default.Share, contentDescription = "Share Graphic")
            }
         }
      )

      Surface(
         color = MaterialTheme.colors.screenBackground,
         modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
      ) {
         Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
         ) {
            SubcomposeAsyncImage(
               model = url,
               loading = { CircularProgressIndicator(Modifier.padding(16.dp)) },
               contentScale = ContentScale.Fit,
               modifier = Modifier.fillMaxSize(),
               contentDescription = title
            )
         }
      }
   }
}