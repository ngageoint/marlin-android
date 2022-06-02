package mil.nga.msi.ui.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import mil.nga.msi.R

class ClusterRenderer(
   context: Context,
   map: GoogleMap,
   clusterManager: ClusterManager<AnnotationItem>
): DefaultClusterRenderer<AnnotationItem>(context, map, clusterManager) {
   init {
      setAnimation(false)
   }

   override fun onBeforeClusterItemRendered(item: AnnotationItem, markerOptions: MarkerOptions) {
      val resource = when (item.annotation.type) {
         Annotation.Type.ASAM -> R.drawable.asam_map_marker_24dp
         Annotation.Type.MODU -> R.drawable.modu_map_marker_24dp
      }
      markerOptions.icon(BitmapDescriptorFactory.fromResource(resource))
   }
}