package mil.nga.msi.ui.about

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import mil.nga.msi.R
import mil.nga.msi.packagemanager.getPackageInfoCompat
import mil.nga.msi.type.Developer
import mil.nga.msi.ui.main.TopBar

@Composable
fun AboutScreen(
   onClose: () -> Unit,
   onDisclaimer: () -> Unit,
   onPrivacy: () -> Unit,
   onContact: () -> Unit,
   viewModel: AboutViewModel = hiltViewModel()
) {
   val developer by viewModel.developer.observeAsState()

   Column {
      TopBar(
         title = AboutRoute.Main.title,
         navigationIcon = Icons.Filled.Close,
         onNavigationClicked = { onClose() }
      )

      About(
         developer = developer,
         onDisclaimer = onDisclaimer,
         onPrivacy = onPrivacy,
         onContact = onContact,
         onDeveloperMode = { viewModel.setDeveloperMode() },
         onShowNoLocationNavigationWarnings = { viewModel.setShowNoLocationNavigationWarnings(it) }
      )
   }
}

@Composable
private fun About(
   developer: Developer?,
   onDisclaimer: () -> Unit,
   onPrivacy: () -> Unit,
   onContact: () -> Unit,
   onDeveloperMode: () -> Unit,
   onShowNoLocationNavigationWarnings: (Boolean) -> Unit
) {
   val context = LocalContext.current
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
         Disclaimer { onDisclaimer() }
         HorizontalDivider(Modifier.padding(start = 16.dp))
         Privacy { onPrivacy() }
         HorizontalDivider(Modifier.padding(start = 16.dp))
         Acknowledgements {
            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
         }
         HorizontalDivider(Modifier.padding(start = 16.dp))
         Contact { onContact() }
         HorizontalDivider(Modifier.padding(start = 16.dp))
         Version(
            developer = developer,
            onDeveloperMode = onDeveloperMode
         )

         if (developer?.showDeveloperMode == true) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Text(
                  text = "DEVELOPER TOOLS",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 8.dp)
               )
            }

            DeveloperTools(
               developer = developer,
               onShowNoLocationNavigationWarnings = onShowNoLocationNavigationWarnings
            )
         }
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
            .semantics { testTag = "disclaimer_column" }
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
private fun Privacy(
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
            .semantics { testTag = "privacy_column" }
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically
         ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Icon(
                  Icons.Default.Policy,
                  modifier = Modifier.padding(end = 16.dp),
                  contentDescription = "About"
               )
            }

            Text(
               text = AboutRoute.Privacy.title,
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium
            )
         }
      }
   }
}

@Composable
private fun Acknowledgements(
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
                  Icons.Default.Handshake,
                  modifier = Modifier.padding(end = 16.dp),
                  contentDescription = "Acknowledgements"
               )
            }

            Text(
               text = AboutRoute.Licenses.title,
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
            .semantics { testTag = "contact_column" }
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
private fun Version(
   developer: Developer?,
   onDeveloperMode: () -> Unit
) {
   val context =  LocalContext.current
   val packageInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
   var developerModeAttempts by remember { mutableIntStateOf(0) }

   Surface {
      Column(
         verticalArrangement = Arrangement.Center,
         modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .clickable(developer?.showDeveloperMode != true) {
               developerModeAttempts += 1
               if (developerModeAttempts >= 5) {
                  onDeveloperMode()
                  Toast
                     .makeText(context, "Developer tools enabled.", Toast.LENGTH_LONG)
                     .show()
               }
            }
            .padding(horizontal = 16.dp)
            .semantics { testTag = "version_column" }
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

@Composable
private fun DeveloperTools(
   developer: Developer?,
   onShowNoLocationNavigationWarnings: (Boolean) -> Unit
) {
   NavigationWarning(
      showNoLocationNavigationWarnings = developer?.showNonParsedNavigationWarnings == true,
      onShowNoLocationNavigationWarnings = onShowNoLocationNavigationWarnings
   )
}

@Composable
private fun NavigationWarning(
   showNoLocationNavigationWarnings: Boolean,
   onShowNoLocationNavigationWarnings: (Boolean) -> Unit,
) {
   Surface(
      Modifier.semantics { testTag = "navigation_warning_surface" }
   ) {
      Column(
         verticalArrangement = Arrangement.Center,
         modifier = Modifier.fillMaxWidth()
      ) {
         Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
         ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
               Icon(
                  Icons.Default.LocationOff,
                  modifier = Modifier
                     .padding(end = 10.dp)
                     .size(28.dp),
                  contentDescription = "No Location"
               )
            }

            Text(
               text = "Show Navigation Warnings With No Parsed Location",
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Medium,
               modifier = Modifier.weight(1f)
            )

            Switch(
               checked = showNoLocationNavigationWarnings,
               onCheckedChange = {
                  onShowNoLocationNavigationWarnings(!showNoLocationNavigationWarnings)
               }
            )
         }
      }
   }
}

