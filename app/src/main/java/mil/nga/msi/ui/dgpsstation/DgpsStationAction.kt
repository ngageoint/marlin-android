package mil.nga.msi.ui.dgpsstation

import mil.nga.msi.ui.navigation.NavPoint

sealed class DgpsStationAction {
   class Share(val text: String) : DgpsStationAction()
   class Zoom(val point: NavPoint): DgpsStationAction()
   class Location(val text: String): DgpsStationAction()
}