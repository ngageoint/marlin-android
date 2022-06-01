package mil.nga.msi

sealed class Routes(val title: String, val route: String) {
   sealed class Asam(val title: String, val route: String) {
      object Details: Asam("ASAM", "asamDetails")
      object Sheet: Asam("ASAM", "asamSheet")
   }

   object Modu : Routes("MODU", "moduSheet")
}