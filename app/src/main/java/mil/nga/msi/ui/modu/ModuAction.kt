package mil.nga.msi.ui.modu

import mil.nga.msi.ui.navigation.NavPoint

sealed class ModuAction {
   class Share(val text: String) : ModuAction()
   class Zoom(val point: NavPoint): ModuAction()
   class Location(val text: String): ModuAction()
}