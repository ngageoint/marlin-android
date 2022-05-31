package mil.nga.msi

sealed class BottomSheet(val title: String, val route: String) {
   object Asam : DrawerScreen("ASAM", "asam")
   object Modu : DrawerScreen("MODU", "modu")
}