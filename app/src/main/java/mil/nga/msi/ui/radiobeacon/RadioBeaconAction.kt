package mil.nga.msi.ui.radiobeacon

import mil.nga.msi.ui.navigation.NavPoint

sealed class RadioBeaconAction {
   class Share(val text: String) : RadioBeaconAction()
   class Zoom(val point: NavPoint): RadioBeaconAction()
   class Location(val text: String): RadioBeaconAction()
}