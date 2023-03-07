package mil.nga.msi.ui.map.settings.layers

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
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.layer.LayerRepository
import javax.inject.Inject

@HiltViewModel
class MapNewLayerViewModel @Inject constructor(
 private val layerRepository: LayerRepository
): ViewModel() {
   private var tileUrlJob: Job? = null
   private val _tileUrl = MutableLiveData<Uri>()
   val tileUrl: LiveData<Uri> = _tileUrl

   private var wmsUrlJob: Job? = null
   private val _wmsCapabilities = MutableLiveData<WMSCapabilities?>()
   val wmsCapabilities: LiveData<WMSCapabilities?> = _wmsCapabilities

   fun onLayerUrl(url: String) {
      tileUrlJob?.cancel()
      tileUrlJob = viewModelScope.launch {
         delay(DEBOUNCE_TIMEOUT_MILLIS)
         if (layerRepository.getTile(url)) {
            _tileUrl.value = Uri.parse(url)
         }
      }

      wmsUrlJob?.cancel()
      wmsUrlJob = viewModelScope.launch {
         delay(DEBOUNCE_TIMEOUT_MILLIS)
         _wmsCapabilities.value = layerRepository.getWMSCapabilities(url)
      }
   }

   companion object {
      private const val DEBOUNCE_TIMEOUT_MILLIS = 300L
   }
}