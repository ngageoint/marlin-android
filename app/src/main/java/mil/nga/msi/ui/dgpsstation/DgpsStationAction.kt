package mil.nga.msi.ui.dgpsstation

import mil.nga.msi.ui.navigation.Point

sealed class DgpsStationAction {
   class Share(val text: String) : DgpsStationAction()
   class Zoom(val point: Point): DgpsStationAction()
   class Location(val text: String): DgpsStationAction()
}