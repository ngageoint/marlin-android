package mil.nga.msi.ui.port

import mil.nga.msi.ui.navigation.Point

sealed class PortAction {
   class Share(val text: String) : PortAction()
   class Zoom(val point: Point): PortAction()
   class Location(val text: String): PortAction()
}