package mil.nga.msi.ui.map.settings.layers.grid

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

   private val _fetchError = MutableLiveData(false)
   val fetchError: LiveData<Boolean> = _fetchError

   private var tileUrlJob: Job? = null
   private val _tileUrl = MutableLiveData<Uri?>()
   val tileUrl: LiveData<Uri?> = _tileUrl

   fun setUrl(url: String) {
      tileUrlJob?.cancel()
      tileUrlJob = viewModelScope.launch {
         delay(DEBOUNCE_TIMEOUT_MILLIS)
         if (layerRepository.getTile(url)) {
            _tileUrl.value = Uri.parse(url)
            _fetchError.value = false
         } else {
            _tileUrl.value = null
            _fetchError.value = true
         }
      }
   }

   fun setId(id: Long) {
      viewModelScope.launch(Dispatchers.IO) {
         _layer.postValue(layerRepository.getLayer(id))
      }
   }

   suspend fun createLayer(
      name: String,
      type: LayerType,
      url: String,
      minZoom: Int?,
      maxZoom: Int?
   ) {
      val layer = Layer(
         name = name,
         type = type,
         url = url,
         visible = true,
         minZoom = minZoom,
         maxZoom = maxZoom
      )

      layerRepository.createLayer(layer)
   }

   suspend fun updateLayer(
      layer: Layer
   ) {
      layerRepository.updateLayer(layer)
   }

   companion object {
      private const val DEBOUNCE_TIMEOUT_MILLIS = 300L
   }
}