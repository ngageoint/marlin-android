package mil.nga.msi.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun SettingsScreen(
   close: () -> Unit,
   onDisclaimer: () -> Unit,
   onAbout: () -> Unit
) {
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = SettingsRoute.Main.title,
         buttonIcon = Icons.Filled.Close,
         onButtonClicked = { close() }
      )

      Settings(
         onDisclaimer = onDisclaimer,
         onAbout = onAbout
      )
   }
}

@Composable
private fun Settings(
   onDisclaimer: () -> Unit,
   onAbout: () -> Unit
) {
   Column(
      modifier = Modifier
         .fillMaxHeight()
         .background(MaterialTheme.colors.screenBackground)
   ) {
      Column(
         Modifier
            .padding(top = 32.dp)
            .background(MaterialTheme.colors.surface)
      ) {
         Disclaimer() { onDisclaimer() }
         Divider(Modifier.padding(start = 16.dp))
//         About() { onAbout() }
//         Divider(Modifier.padding(start = 16.dp))
         Version()
      }
   }
}

@Composable
private fun Disclaimer(
   onTap: () -> Unit
) {
   Column(
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
         .height(48.dp)
         .fillMaxWidth()
         .clickable { onTap() }
         .padding(horizontal = 16.dp)
   ) {
      Text(
         text = SettingsRoute.Disclaimer.title,
         style = MaterialTheme.typography.h6,
         fontWeight = FontWeight.Normal
      )
   }
}

@Composable
private fun About(
   onTap: () -> Unit
) {
   Column(
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
         .height(48.dp)
         .fillMaxWidth()
         .clickable { onTap() }
         .padding(horizontal = 16.dp)
   ) {
      Text(
         text = SettingsRoute.About.title,
         style = MaterialTheme.typography.h6,
         fontWeight = FontWeight.Normal
      )
   }
}

@Composable
private fun Version() {
   val context =  LocalContext.current
   val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

   Column(
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
         .height(48.dp)
         .fillMaxWidth()
         .padding(horizontal = 16.dp)
   ) {
      Text(
         text = "Marlin Version ${packageInfo.versionName}",
         style = MaterialTheme.typography.h6,
         fontWeight = FontWeight.Normal
      )
   }
}

