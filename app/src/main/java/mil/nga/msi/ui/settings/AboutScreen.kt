package mil.nga.msi.ui.settings

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
   Column {
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
   val scrollState = rememberScrollState()

   Surface(
      color = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.fillMaxSize()
   ) {
      Column(
         Modifier
            .fillMaxHeight()
            .padding(top = 32.dp)
            .verticalScroll(scrollState)
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
   Surface {
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
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Icon(
                  Icons.Default.PrivacyTip,
                  modifier = Modifier.padding(end = 16.dp),
                  contentDescription = "About"
               )
            }

            Text(
               text = AboutRoute.Disclaimer.title,
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium
            )
         }
      }
   }
}

@Composable
private fun Contact(
   onTap: () -> Unit
) {
   Surface {
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
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Icon(
                  Icons.Default.Mail,
                  modifier = Modifier.padding(end = 16.dp),
                  contentDescription = "About"
               )
            }

            Text(
               text = "Contact Us",
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium
            )
         }
      }
   }
}

@Composable
private fun Version() {
   val context =  LocalContext.current
   val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

   Surface {
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
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               val bitmap =
                  AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_marlin_600dp)!!
                     .toBitmap().asImageBitmap()
               Icon(
                  bitmap,
                  modifier = Modifier
                     .padding(end = 10.dp)
                     .size(28.dp),
                  contentDescription = "About"
               )
            }

            Text(
               text = "Marlin Version ${packageInfo.versionName}",
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium
            )
         }
      }
   }
}

