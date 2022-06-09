package mil.nga.msi.ui.navigation

interface Route {
   val name: String
   val title: String

   companion object {
      fun fromRoute(route: String?): Route? {
         return when (route?.substringBefore("/")) {
            else -> null
         }
      }
   }
}