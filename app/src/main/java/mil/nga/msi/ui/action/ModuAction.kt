package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.ui.asam.AsamRoute

sealed class ModuAction(): Action() {
   class Tap(val modu: Modu): ModuAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${AsamRoute.Detail.name}?name=${modu.name}")
      }
   }


   class Location(val text: String): ModuAction()
   class Share(val modu: Modu) : ModuAction()
}