package mil.nga.msi.ui.modu

import mil.nga.msi.ui.navigation.Point

sealed class ModuAction {
   class Share(val text: String) : ModuAction()
   class Zoom(val point: Point): ModuAction()
   class Location(val text: String): ModuAction()
}