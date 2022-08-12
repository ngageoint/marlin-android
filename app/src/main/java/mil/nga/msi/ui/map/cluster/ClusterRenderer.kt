package mil.nga.msi.ui.map.cluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ClusterRenderer(
   context: Context,
   map: GoogleMap,
   clusterManager: ClusterManager<MapAnnotation>
): DefaultClusterRenderer<MapAnnotation>(context, map, clusterManager) {
   init {
      setAnimation(false)
   }

   override fun onBeforeClusterItemRendered(annotation: MapAnnotation, markerOptions: MarkerOptions) {
      annotation.key.type.icon?.let { icon ->
         markerOptions.icon(BitmapDescriptorFactory.fromResource(icon))
      }
   }
}