package mil.nga.msi.ui.port

import mil.nga.msi.ui.navigation.NavPoint

sealed class PortAction {
   class Share(val text: String) : PortAction()
   class Zoom(val point: NavPoint): PortAction()
   class Location(val text: String): PortAction()
}