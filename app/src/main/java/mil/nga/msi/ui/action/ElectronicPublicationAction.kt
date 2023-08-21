package mil.nga.msi.ui.action

import androidx.navigation.NavController
import mil.nga.msi.datasource.electronicpublication.ElectronicPublication
import mil.nga.msi.ui.electronicpublication.ElectronicPublicationRoute

sealed class ElectronicPublicationAction(): Action() {
   class Tap(private val electronicPublication: ElectronicPublication): ElectronicPublicationAction() {
      override fun navigate(navController: NavController) {
         navController.navigate("${ElectronicPublicationRoute.Detail.name}?s3Key=${electronicPublication.s3Key}")
      }
   }
}

