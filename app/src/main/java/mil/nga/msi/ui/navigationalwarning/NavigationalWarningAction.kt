package mil.nga.msi.ui.navigationalwarning

import com.google.android.gms.maps.model.LatLngBounds

sealed class NavigationalWarningAction {
   class Share(val text: String) : NavigationalWarningAction()
   class Zoom(val bounds: LatLngBounds) : NavigationalWarningAction()
}