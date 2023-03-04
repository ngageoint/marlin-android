package mil.nga.msi.ui.map.settings.layers

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class MapLayersViewModel @Inject constructor(
   private val layerRepository: LayerRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val layers = combine(userPreferencesRepository.layers, layerRepository.observeLayers()) { order, layers ->
      val orderById = order.withIndex().associate { (index, it) -> it to index }
      layers.sortedBy { orderById[it.id.toInt()] }
   }.asLiveData()

   fun setLayerOrder(layers: List<Int>) {
      viewModelScope.launch {
         userPreferencesRepository.setLayers(layers)
      }
   }

   fun enableLayer(layer: Layer, enabled: Boolean) {
      viewModelScope.launch {
         layerRepository.enabledLayer(layer, enabled)
      }
   }

   fun deleteLayer(layer: Layer) {
      viewModelScope.launch {
         layerRepository.deleteLayer(layer)
      }
   }
}