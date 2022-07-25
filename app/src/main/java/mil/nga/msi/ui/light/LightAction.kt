package mil.nga.msi.ui.light

import mil.nga.msi.ui.navigation.Point

sealed class LightAction {
   class Share(val text: String) : LightAction()
   class Zoom(val point: Point): LightAction()
   class Location(val text: String): LightAction()
}