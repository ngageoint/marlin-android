package mil.nga.msi.repository.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem

data class NavigationalWarningKey(
   val number: Int,
   val year: Int,
   val navigationArea: NavigationArea
) {
   companion object {
      fun fromNavigationWarning(warning: NavigationalWarning): NavigationalWarningKey {
         return NavigationalWarningKey(warning.number, warning.year, warning.navigationArea)
      }

      fun fromNavigationWarning(warning: NavigationalWarningListItem): NavigationalWarningKey {
         return NavigationalWarningKey(warning.number, warning.year, warning.navigationArea)
      }
   }
}