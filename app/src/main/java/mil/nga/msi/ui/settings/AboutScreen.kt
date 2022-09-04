package mil.nga.msi.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar

@Composable
fun AboutScreen(
   close: () -> Unit
) {
   val scrollState = rememberScrollState()
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = SettingsRoute.About.title,
         buttonIcon = Icons.Filled.ArrowBack,
         onButtonClicked = { close() }
      )

      Column(
         Modifier
            .padding(all = 16.dp)
            .verticalScroll(scrollState)
      ) {
         Text(
            text = "Version Here",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
         )
      }
   }
}
