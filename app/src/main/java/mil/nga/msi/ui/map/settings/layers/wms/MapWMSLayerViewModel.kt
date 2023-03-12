package mil.nga.msi.ui.map.settings.layers.wms

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

data class WmsState(
   val layer: Layer? = null,
   val baseUrl: String,
   val layers: List<String>,
   val wmsCapabilities: WMSCapabilities
) {
   val mapUrl = mapUrl()

   private fun mapUrl(): String {
      val format = wmsCapabilities.capability?.request?.map?.getImageFormat() ?: "image/png"
      val version = wmsCapabilities.version
      val epsg = if (version == "1.3" || version == "1.3.0") "CRS" else "SRS"

      return Uri.parse(baseUrl).buildUpon()
         .appendQueryParameter("REQUEST", "GetMap")
         .appendQueryParameter("SERVICE", "WMS")
         .appendQueryParameter(epsg, "EPSG:3857")
         .appendQueryParameter("WIDTH", "256")
         .appendQueryParameter("HEIGHT", "256")
         .appendQueryParameter("FORMAT", format)
         .appendQueryParameter("TRANSPARENT", "false")
         .appendQueryParameter("LAYERS", layers.joinToString(","))
         .build()
         .toString()
   }
}

// TODO should I just inject 2 view models, should I put all this in one view model?
@HiltViewModel
class MapWMSLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {
   private var wmsUrlJob: Job? = null
   private val _wmsState = MutableLiveData<WmsState?>()
   val wmsState: LiveData<WmsState?> = _wmsState

   fun setLayerId(id: Long) {
      wmsUrlJob?.cancel()
      wmsUrlJob = viewModelScope.launch {
         delay(DEBOUNCE_TIMEOUT_MILLIS)

         val layer = layerRepository.getLayer(id)

         val uri = Uri.parse(layer.url)
         val baseUrl = "${uri.scheme}://${uri.host}/${uri.path}"
         val layers = uri.getQueryParameters("LAYERS")

         layerRepository.getWMSCapabilities(baseUrl)?.let { wmsCapabilities ->
            _wmsState.postValue(
               WmsState(
                  layer = layer,
                  baseUrl = baseUrl,
                  layers = layers,
                  wmsCapabilities = wmsCapabilities
               )
            )
         }
      }
   }

   fun setUrl(url: String) {
      viewModelScope.launch {
         layerRepository.getWMSCapabilities(url)?.let { wmsCapabilities ->
            _wmsState.postValue(
               WmsState(
                  baseUrl = url,
                  layers = emptyList(),
                  wmsCapabilities = wmsCapabilities
               )
            )
         }
      }
   }

   fun setLayer(layer: String, enabled: Boolean) {
      wmsState.value?.let { wmsState ->
         val layers = wmsState.layers.toMutableSet().apply {
            if (enabled) add(layer) else remove(layer)
         }.toList()

         val state = wmsState.copy(
            layers = layers
         )
         Log.i("Billy", "new state is $state")
         _wmsState.value = state
      }
   }

   suspend fun saveLayer(layer: Layer) {
      if (layer.id == 0L) {
         layerRepository.createLayer(layer)
      } else {
         layerRepository.updateLayer(layer)
      }
   }

   companion object {
      private const val DEBOUNCE_TIMEOUT_MILLIS = 300L
   }
}