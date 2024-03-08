package mil.nga.msi.ui.map.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.coordinate.CoordinateSystem
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.ui.map.BaseMapType
import mil.nga.msi.ui.map.search.SearchProvider
import javax.inject.Inject

@HiltViewModel
class MapSettingsViewModel @Inject constructor(
   private val repository: MapRepository
): ViewModel() {
   val baseMap = repository.baseMapType.asLiveData()
   fun setBaseLayer(baseMapType: BaseMapType) {
      viewModelScope.launch {
         repository.setBaseMapType(baseMapType)
      }
   }

   val gars = repository.gars.asLiveData()
   fun setGARS(enabled: Boolean) {
      viewModelScope.launch {
         repository.setGARS(enabled)
      }
   }

   val mgrs = repository.mgrs.asLiveData()
   fun setMGRS(enabled: Boolean) {
      viewModelScope.launch {
         repository.setMGRS(enabled)
      }
   }

   val showLocation = repository.showLocation.asLiveData()
   fun setShowLocation(enabled: Boolean) {
      viewModelScope.launch {
         repository.setShowLocation(enabled)
      }
   }

   val showScale = repository.showScale.asLiveData()
   fun setShowScale(enabled: Boolean) {
      viewModelScope.launch {
         repository.setShowScale(enabled)
      }
   }

   val coordinateSystem = repository.coordinateSystem.asLiveData()
   fun setCoordinateSystem(coordinateSystem: CoordinateSystem) {
      viewModelScope.launch {
         repository.setCoordinateSystem(coordinateSystem)
      }
   }

   val searchProvider = repository.searchProvider.asLiveData()
   fun setSearchProvider(searchProvider: SearchProvider) {
      viewModelScope.launch {
         repository.setSearchProvider(searchProvider)
      }
   }
}