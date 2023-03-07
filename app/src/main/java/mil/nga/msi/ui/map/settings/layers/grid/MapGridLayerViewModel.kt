package mil.nga.msi.ui.map.settings.layers.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class MapGridLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {

   fun saveLayer(
      name: String,
      type: LayerType,
      url: String
   ) {
      viewModelScope.launch {
         val layer = Layer(
            name = name,
            displayName = name,
            type = type,
            url = url,
            visible = true
         )

         layerRepository.createLayer(layer)
      }
   }
}