package mil.nga.msi.ui.asam

import mil.nga.msi.ui.navigation.NavPoint

sealed class AsamAction {
   class Share(val text: String) : AsamAction()
   class Zoom(val point: NavPoint): AsamAction()
   class Location(val text: String): AsamAction()
}