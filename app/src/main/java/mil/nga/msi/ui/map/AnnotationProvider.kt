package mil.nga.msi.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mil.nga.msi.location.LocationProvider
import mil.nga.msi.ui.map.cluster.MapAnnotation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnotationProvider @Inject constructor(val locationProvider: LocationProvider) {
   private val _annotation = MutableLiveData<MapAnnotation?>()
   val annotation: LiveData<MapAnnotation?> = _annotation
   fun setMapAnnotation(annotation: MapAnnotation?) {
      _annotation.value = annotation
   }
}