package mil.nga.msi.ui.asam

import mil.nga.msi.ui.navigation.Point

sealed class AsamAction {
   class Share(val text: String) : AsamAction()
   class Zoom(val point: Point): AsamAction()
   class Location(val text: String): AsamAction()
}