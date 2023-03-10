package mil.nga.msi.ui.map.settings.layers.grid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class MapGridLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {

   private val _layer = MutableLiveData<Layer>()
   val layer: LiveData<Layer> = _layer

   fun setId(id: Long) {
      viewModelScope.launch(Dispatchers.IO) {
         _layer.postValue(layerRepository.getLayer(id))
      }
   }

   fun createLayer(
      name: String,
      type: LayerType,
      url: String
   ) {
      viewModelScope.launch {
         val layer = Layer(
            name = name,
            type = type,
            url = url,
            visible = true
         )

         layerRepository.createLayer(layer)
      }
   }

   fun updateLayer(
      layer: Layer
   ) {
      viewModelScope.launch {
         layerRepository.updateLayer(layer)
      }
   }
}