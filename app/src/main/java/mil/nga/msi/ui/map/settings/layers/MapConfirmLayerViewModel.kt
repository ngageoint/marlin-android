package mil.nga.msi.ui.map.settings.layers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class MapConfirmLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {

  fun saveLayer(layer: Layer) {
   viewModelScope.launch {
       layerRepository.createLayer(layer)
     }
  }
}