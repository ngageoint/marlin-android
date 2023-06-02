package mil.nga.msi.ui.report

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import mil.nga.msi.ui.main.TopBar

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ReportPage(
   reportRoute: ReportRoute,
   close: () -> Unit
) {
   Column(
      Modifier.fillMaxSize()
   ) {
      TopBar(
         title = reportRoute.title,
         navigationIcon = Icons.Default.Close,
         onNavigationClicked = { close() }
      )

      Column(
         modifier = Modifier.fillMaxHeight()
      ) {
         val state = rememberWebViewState(reportRoute.url)
         WebView(
            state = state,
            onCreated = { it.settings.javaScriptEnabled = true }
         )

         if (state.isLoading) {
            Box(
               modifier = Modifier.fillMaxSize()
            ) {
               Box(
                  modifier = Modifier
                     .align(Alignment.Center)
                     .padding(bottom = 144.dp)
               ) {
                  CircularProgressIndicator(Modifier.size(90.dp))
               }

               Text(
                  text = "Loading ${reportRoute.shortTitle}...",
                  style = MaterialTheme.typography.headlineSmall,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.align(Alignment.Center)
               )
            }
         }
      }
   }
}