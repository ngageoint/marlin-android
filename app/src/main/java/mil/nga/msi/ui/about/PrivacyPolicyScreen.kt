package mil.nga.msi.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mil.nga.msi.ui.main.TopBar

@Composable
fun PrivacyPolicyScreen(
   close: () -> Unit
) {
   val scrollState = rememberScrollState()
   Column(modifier = Modifier.fillMaxSize()) {
      TopBar(
         title = AboutRoute.Privacy.title,
         navigationIcon = Icons.Default.ArrowBack,
         onNavigationClicked = { close() }
      )

      Column(
         Modifier
            .padding(all = 16.dp)
            .verticalScroll(scrollState)
      ) {
         PrivacyPolicy()
      }
   }
}

@Composable
private fun PrivacyPolicy() {
   Column(Modifier.padding(vertical = 8.dp)) {
      Text(
         text = "NGA Marlin Privacy Policy",
         style = MaterialTheme.typography.titleLarge,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "This App is exclusively licensed to and operated by the National Geospatial-Intelligence Agency (NGA). Our Privacy Policy informs you, the user, what personally identifiable information (PII) we collect when you use this App, how we use and share the PII we collect, and how we protect your privacy. Further information on NGA's privacy practices can be found at http://www.nga.mil/, at the \"Privacy and Security\" tab.",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "If you have any questions about this Policy or our privacy practices, please contact us using the following information: publicaffairs@nga.mil. Effective Date: 20 December 2022",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "Your Consent: Please review NGA's Privacy Policy before using this App. By using this App you are consenting to the collection, use, and disclosure of your PII as set forth in this Policy. If you do not agree to be bound by this Policy, you may not access or use this App.",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
         text = "Privacy Notices",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "This Policy may be supplemented or amended from time to time and NGA will notify you by issuing “privacy notices.” These Privacy Notices will provide a level of detail describing the changes in our policy and how they may affect you. For example, this App may issue Privacy Notices providing details about changes in the function and use of the PII we collect, and why we need that information.",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
         text = "Types of Information We Collect",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "NGA will collect NO PII or information from your mobile device. NGA neither collects nor acquires any user names, passwords or other login information or personal information from Google.",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
         text = "Links to Third-Party Websites",
         style = MaterialTheme.typography.titleMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
         text = "This App may contain links to websites operated by other organizations. This Policy does not apply to PII collected on any of these third-party websites. When you access third-party websites through a link on this App, please take a few minutes to review the privacy policy posted on that site. This App uses the Google Maps/Google Earth API. It incorporates by reference the Google Privacy Policy, which can be viewed at http://www.google.com/privacy.html, as amended by Google from time to time.",
         style = MaterialTheme.typography.bodyMedium,
         fontWeight = FontWeight.Medium,
         modifier = Modifier.padding(bottom = 32.dp)
      )
   }
}

