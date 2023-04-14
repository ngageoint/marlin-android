package mil.nga.msi.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import mil.nga.msi.ui.navigation.Route

sealed class AboutRoute(
   override val name: String,
   override val title: String,
   override val shortTitle: String = title,
   override val color: Color = Color(0xFF000000)
): Route {
   object Main: AboutRoute("about", "About")
   object List: AboutRoute("about/list", "Settings")
   object Licenses: AboutRoute("about/licenses", "Open Source Licenses")
   object Disclaimer: AboutRoute("about/disclaimer", "Disclaimer")
}

fun NavGraphBuilder.settingsGraph(
   navController: NavController,
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
            close = { navController.popBackStack() },
            onDisclaimer = {
               navController.navigate(AboutRoute.Disclaimer.name)
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
            close = { navController.popBackStack() }
         )
      }
   }
}