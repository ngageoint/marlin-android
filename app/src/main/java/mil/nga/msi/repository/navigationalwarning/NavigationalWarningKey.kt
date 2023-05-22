package mil.nga.msi.repository.navigationalwarning

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem

@Serializable
@Parcelize
data class NavigationalWarningKey(
   val number: Int,
   val year: Int,
   val navigationArea: NavigationArea
): Parcelable {
   fun id(): String {
      return "${number}--${year}--${navigationArea.name}"
   }

   companion object {
      fun fromId(id: String): NavigationalWarningKey {
         val (number, year, navigationArea) = id.split("--")
         return NavigationalWarningKey(number.toInt(), year.toInt(), NavigationArea.valueOf(navigationArea))
      }

      fun fromNavigationWarning(warning: NavigationalWarning): NavigationalWarningKey {
         return NavigationalWarningKey(warning.number, warning.year, warning.navigationArea)
      }

      fun fromNavigationWarning(warning: NavigationalWarningListItem): NavigationalWarningKey {
         return NavigationalWarningKey(warning.number, warning.year, warning.navigationArea)
      }
   }
}