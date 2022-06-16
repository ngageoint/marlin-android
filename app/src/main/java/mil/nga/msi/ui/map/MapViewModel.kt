package mil.nga.msi.ui.map

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.type.MapLocation
import mil.nga.msi.ui.map.cluster.MapAnnotation
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
   asamRepository: AsamRepository,
   moduRepository: ModuRepository,
   val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

   val baseMap = userPreferencesRepository.baseMapType.asLiveData()
   val mapLocation = userPreferencesRepository.mapLocation.asLiveData()
   val mgrs = userPreferencesRepository.mgrs.asLiveData()

   suspend fun setMapLocation(mapLocation: MapLocation) =userPreferencesRepository.setMapLocation(mapLocation)

   private val _mapAnnotations = mutableMapOf<MapAnnotation.Type, List<MapAnnotation>>()
   val mapAnnotations = MediatorLiveData<List<MapAnnotation>>().apply {
      addSource(asamRepository.asamMapItems.asLiveData()) { asams: List<AsamMapItem> ->
         _mapAnnotations[MapAnnotation.Type.ASAM] = asams.map { MapAnnotation.fromAsam(it) }
         value = _mapAnnotations.flatMap { entry ->  entry.value }
      }
      addSource(moduRepository.moduMapItems.asLiveData()) { modus: List<ModuMapItem> ->
         _mapAnnotations[MapAnnotation.Type.MODU] = modus.map { MapAnnotation.fromModu(it) }
         value = _mapAnnotations.flatMap { entry ->  entry.value }
      }
   }
}