package mil.nga.msi

sealed class Routes {
   sealed class Asam(val route: String) {
      object Details: Asam("asamDetails")
      object Sheet: Asam("asamSheet")
   }
   sealed class Modu(val route: String) {
      object Details: Asam("moduDetails")
      object Sheet: Asam("moduSheet")
   }
}