package mil.nga.msi.ui.navigation

import androidx.compose.ui.graphics.Color

interface Route {
   val name: String
   val title: String
   val color: Color

   companion object {
      fun fromRoute(route: String?): Route? {
         return when (route?.substringBefore("/")) {
            else -> null
         }
      }
   }
}