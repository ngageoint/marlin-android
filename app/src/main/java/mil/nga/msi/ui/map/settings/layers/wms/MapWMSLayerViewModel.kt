package mil.nga.msi.ui.map.settings.layers.wms

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class MapWMSLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {
   suspend fun saveLayer(
      url: String,
      name: String
   ) {
      val layer = Layer(
         url = url,
         name = name,
         displayName = name,
         type = LayerType.WMS
      )

      layerRepository.createLayer(layer)
   }
}