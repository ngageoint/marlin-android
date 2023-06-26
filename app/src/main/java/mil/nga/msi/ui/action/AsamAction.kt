package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.ui.asam.AsamRoute

sealed class AsamAction(): Action() {
   class Tap(val asam: Asam): AsamAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${AsamRoute.Detail.name}?reference=${asam.reference}")
      }
   }

   class Share(val asam: Asam): AsamAction()
   class Location(val text: String): AsamAction()
}

