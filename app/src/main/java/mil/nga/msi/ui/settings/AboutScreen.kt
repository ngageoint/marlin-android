package mil.nga.msi.ui.settings

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import mil.nga.msi.R
import mil.nga.msi.ui.main.TopBar
import mil.nga.msi.ui.theme.screenBackground

@Composable
fun AboutScreen(
   close: () -> Unit,
   onDisclaimer: () -> Unit,
   onContact: () -> Unit,
) {
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = AboutRoute.Main.title,
         navigationIcon = Icons.Filled.Close,
         onNavigationClicked = { close() }
      )

      About(
         onDisclaimer = onDisclaimer,
         onContact = onContact
      )
   }
}

@Composable
private fun About(
   onDisclaimer: () -> Unit,
   onContact: () -> Unit
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
         Contact() { onContact() }
         Divider(Modifier.padding(start = 16.dp))
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
      Row(
         verticalAlignment = Alignment.CenterVertically
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
               Icons.Default.PrivacyTip,
               modifier = Modifier.padding(end = 8.dp),
               contentDescription = "About"
            )
         }

         Text(
            text = AboutRoute.Disclaimer.title,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
         )
      }
   }
}

@Composable
private fun Contact(
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
      Row(
         verticalAlignment = Alignment.CenterVertically
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
               Icons.Default.Mail,
               modifier = Modifier.padding(end = 8.dp),
               contentDescription = "About"
            )
         }

         Text(
            text = "Contact Us",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
         )
      }
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
      Row(
         verticalAlignment = Alignment.CenterVertically
      ) {
         CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            val bitmap = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_marlin_600dp)!!.toBitmap().asImageBitmap()
            Icon(
               bitmap,
               modifier = Modifier
                  .padding(end = 2.dp)
                  .size(28.dp),
               contentDescription = "About"
            )
         }

         Text(
            text = "Marlin Version ${packageInfo.versionName}",
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
         )
      }
   }
}

