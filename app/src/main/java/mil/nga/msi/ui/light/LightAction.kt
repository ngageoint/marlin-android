package mil.nga.msi.ui.light

import mil.nga.msi.ui.navigation.NavPoint

sealed class LightAction {
   class Share(val text: String) : LightAction()
   class Zoom(val point: NavPoint): LightAction()
   class Location(val text: String): LightAction()
}