package mil.nga.msi.ui.map

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.cluster.MapAnnotation
import mil.nga.msi.ui.map.overlay.LightTileProvider
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
   asamRepository: AsamRepository,
   moduRepository: ModuRepository,
   val locationPolicy: LocationPolicy,
   val userPreferencesRepository: UserPreferencesRepository,
   private val _lightTileProvider: LightTileProvider
): ViewModel() {

   val baseMap = userPreferencesRepository.baseMapType.asLiveData()
   val mapLocation = userPreferencesRepository.mapLocation.asLiveData()
   val gars = userPreferencesRepository.gars.asLiveData()
   val mgrs = userPreferencesRepository.mgrs.asLiveData()

   private val mapped = userPreferencesRepository.mapped.asLiveData()

   suspend fun setMapLocation(mapLocation: MapLocation) = userPreferencesRepository.setMapLocation(mapLocation)

   val lightTileProvider: LiveData<TileProvider?> = Transformations.map(mapped) { mapped ->
      if (mapped.get(DataSource.LIGHT) == true) {
         _lightTileProvider
      } else null
   }

   private val _mapAnnotations = mutableMapOf<MapAnnotation.Type, List<MapAnnotation>>()
   val mapAnnotations = Transformations.switchMap(mapped) { mapped ->
      Log.i("Billy", "mapped data source updated")
      MediatorLiveData<List<MapAnnotation>>().apply {
         value = emptyList()

         val asamSource = asamRepository.asamMapItems.asLiveData()
         if (mapped[DataSource.ASAM] == true) {
            Log.i("Billy", "add asam source")
            addSource(asamSource) { asams: List<AsamMapItem> ->
               _mapAnnotations[MapAnnotation.Type.ASAM] = asams.map { MapAnnotation.fromAsam(it) }
               value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            removeSource(asamSource)
            _mapAnnotations[MapAnnotation.Type.ASAM] = emptyList()
            value = _mapAnnotations.flatMap { entry ->  entry.value }
         }

         val moduSource = moduRepository.moduMapItems.asLiveData()
         if (mapped[DataSource.MODU] == true) {
            addSource(moduSource) { modus: List<ModuMapItem> ->
               _mapAnnotations[MapAnnotation.Type.MODU] = modus.map { MapAnnotation.fromModu(it) }
               value = _mapAnnotations.flatMap { entry ->  entry.value }
            }
         } else {
            removeSource(moduSource)
            _mapAnnotations[MapAnnotation.Type.MODU] = emptyList()
            value = _mapAnnotations.flatMap { entry ->  entry.value }
         }
      }
   }
}