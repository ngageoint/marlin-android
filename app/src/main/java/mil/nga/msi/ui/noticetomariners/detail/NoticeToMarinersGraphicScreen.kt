package mil.nga.msi.ui.noticetomariners.detail

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic
import mil.nga.msi.ui.main.TopBar

@Composable
fun NoticeToMarinersGraphicScreen(
   graphic: NoticeToMarinersGraphic,
   close: () -> Unit,
   viewModel: NoticeToMarinersGraphicViewModel = hiltViewModel()
) {
   val scope = rememberCoroutineScope()
   val context = LocalContext.current
   val downloading by viewModel.downloading.observeAsState(false)

   Column {
      TopBar(
         title = graphic.title,
         navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
         onNavigationClicked = { close() },
         actions = {
            IconButton(
               enabled = !downloading,
               onClick = {
                  scope.launch {
                     viewModel.getNoticeToMarinersGraphic(graphic)?.let { uri ->
                        val shareIntent = Intent.createChooser(Intent().apply {
                           clipData = ClipData.newRawUri("", uri)
                           action = Intent.ACTION_SEND
                           putExtra(Intent.EXTRA_TITLE, "NTM Graphic")
                           putExtra(Intent.EXTRA_STREAM, uri)
                           type = context.contentResolver.getType(uri)
                           addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }, "NTM Graphic")

                        context.startActivity(shareIntent)
                     }
                  }
               }
            ) {
               Icon(Icons.Default.Share, contentDescription = "Share Graphic")
            }
         }
      )

      Surface(
         modifier = Modifier.fillMaxWidth()
      ) {
         if (downloading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
         }

         Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().weight(1f)
         ) {
            SubcomposeAsyncImage(
               model = graphic.url,
               loading = { CircularProgressIndicator(Modifier.padding(16.dp)) },
               contentScale = ContentScale.Fit,
               modifier = Modifier.fillMaxSize(),
               contentDescription = graphic.title
            )
         }
      }
   }
}