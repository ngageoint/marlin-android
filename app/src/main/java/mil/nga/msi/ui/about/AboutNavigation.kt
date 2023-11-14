package mil.nga.msi.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.MarlinAppState
import mil.nga.msi.ui.navigation.Route

sealed class AboutRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title
): Route {
   data object Main: AboutRoute("about", "About")
   data object List: AboutRoute("about/list", "Settings")
   data object Licenses: AboutRoute("about/acknowledgements", "Acknowledgements")
   data object Disclaimer: AboutRoute("about/disclaimer", "Disclaimer")
   data object Privacy: AboutRoute("about/privacy", "Privacy Policy")
}

fun NavGraphBuilder.settingsGraph(
   appState: MarlinAppState,
   bottomBarVisibility: (Boolean) -> Unit
) {
   navigation(
      route = AboutRoute.Main.name,
      startDestination = AboutRoute.List.name
   ) {
      composable(AboutRoute.List.name) {
         bottomBarVisibility(true)

         val context = LocalContext.current

         AboutScreen(
            onClose = { appState.navController.popBackStack() },
            onDisclaimer = {
               appState.navController.navigate(AboutRoute.Disclaimer.name)
            },
            onPrivacy = {
               appState.navController.navigate(AboutRoute.Privacy.name)
            },
            onContact = {
               val intent = Intent(Intent.ACTION_SENDTO)
               intent.data = Uri.parse("mailto:")
               intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("marlin@nga.mil"))
               intent.putExtra(Intent.EXTRA_SUBJECT, "Whats up")
               startActivity(context, intent, null)
            }
         )
      }

      composable(AboutRoute.Disclaimer.name) {
         bottomBarVisibility(true)

         DisclaimerScreen(
            close = { appState.navController.popBackStack() }
         )
      }

      composable(AboutRoute.Privacy.name) {
         bottomBarVisibility(true)

         PrivacyPolicyScreen(
            close = { appState.navController.popBackStack() }
         )
      }
   }
}