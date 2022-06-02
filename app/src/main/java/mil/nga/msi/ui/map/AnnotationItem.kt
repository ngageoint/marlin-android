package mil.nga.msi.ui.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class AnnotationItem(
   val annotation: Annotation,
   private val position: LatLng
) : ClusterItem {
   override fun getPosition(): LatLng {
      return position
   }

   override fun getTitle(): String? {
      return null
   }

   override fun getSnippet(): String? {
      return null
   }
}