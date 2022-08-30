package mil.nga.msi.ui.radiobeacon

import mil.nga.msi.ui.navigation.Point

sealed class RadioBeaconAction {
   class Share(val text: String) : RadioBeaconAction()
   class Zoom(val point: Point): RadioBeaconAction()
   class Location(val text: String): RadioBeaconAction()
}