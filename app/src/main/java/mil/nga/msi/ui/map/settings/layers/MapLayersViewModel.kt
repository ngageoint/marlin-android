package mil.nga.msi.ui.map.settings.layers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.preferences.SharedPreferencesRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

data class LayerState(
   val layer: Layer,
   val latLngBounds: LatLngBounds? = null
)

@HiltViewModel
class MapLayersViewModel @Inject constructor(
   private val layerRepository: LayerRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val sharedPreferencesRepository: SharedPreferencesRepository
): ViewModel() {
   val layers = combine(userPreferencesRepository.layers, layerRepository.observeLayers()) { order, layers ->
      val orderById = order.withIndex().associate { (index, it) -> it to index }
      layers
         .sortedBy { orderById[it.id.toInt()] }
         .map { layer ->
            val bounds = layer.boundingBox?.let { bounds ->
               LatLngBounds(
                  LatLng(bounds.minLatitude, bounds.minLongitude),
                  LatLng(bounds.maxLatitude, bounds.maxLongitude)
               )
            }

            LayerState(
               layer = layer,
               latLngBounds = bounds
            )
         }
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

   fun addLayer(layer: Layer) {
      viewModelScope.launch {
         layerRepository.insertLayer(layer)
      }
   }

   fun deleteLayer(layer: Layer) {
      viewModelScope.launch {
         layerRepository.deleteLayer(layer)
         sharedPreferencesRepository.deleteLayerCredentials(layer.id)
      }
   }
}