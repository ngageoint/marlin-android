package mil.nga.msi.ui.navigation

sealed class Routes(val route: String) {
   sealed class Pager {
      object Sheet: Routes("pagerSheet")
   }
   sealed class Asam {
      object Details: Routes("asamDetails")
      object Sheet: Routes("asamSheet")
   }
   sealed class Modu {
      object Details: Routes("moduDetails")
      object Sheet: Routes("moduSheet")
   }
}