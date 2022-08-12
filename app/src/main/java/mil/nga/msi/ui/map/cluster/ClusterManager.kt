package mil.nga.msi.ui.map.cluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm

class ClusterManager(
   context: Context,
   map: GoogleMap
) : ClusterManager<MapAnnotation>(context, map) {
   private val annotationMap: MutableMap<MapAnnotation.Key, MapAnnotation> = mutableMapOf()

   init {
      renderer = ClusterRenderer(context, map, this)
      val metrics = context.resources.displayMetrics
      setAlgorithm(NonHierarchicalViewBasedAlgorithm(metrics.widthPixels, metrics.heightPixels))
      map.setOnCameraIdleListener(this)
      map.setOnMarkerClickListener(this)
   }

   override fun addItem(annotation: MapAnnotation): Boolean {
      annotationMap[annotation.key] = annotation
      return super.addItem(annotation)
   }

   override fun addItems(annotations: Collection<MapAnnotation>): Boolean {
      annotationMap.putAll(annotations.associateBy { it.key })
      return super.addItems(annotations)
   }

   override fun removeItem(annotation: MapAnnotation): Boolean {
      annotationMap.remove(annotation.key)
      return super.removeItem(annotation)
   }

   override fun removeItems(annotations: MutableCollection<MapAnnotation>): Boolean {
      annotationMap.keys.forEach { annotationMap.remove(it) }
      return super.removeItems(annotations)
   }

   override fun clearItems() {
      annotationMap.clear()
      super.clearItems()
   }

   fun getClusterItem(key: MapAnnotation.Key): MapAnnotation? {
      return annotationMap[key]
   }
}