package mil.nga.msi.ui.map.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterItem(
   val latLng: LatLng,
   val tag: String? = null,
): ClusterItem {
   override fun getPosition() = latLng
   override fun getTitle() = null
   override fun getSnippet() = null
   override fun getZIndex() = null
}