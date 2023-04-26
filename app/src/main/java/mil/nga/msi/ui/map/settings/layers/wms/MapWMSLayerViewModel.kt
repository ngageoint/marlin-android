package mil.nga.msi.ui.map.settings.layers.wms

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.layer.BoundingBox
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.layer.LayerRepository
import mil.nga.msi.repository.preferences.Credentials
import mil.nga.msi.repository.preferences.SharedPreferencesRepository
import javax.inject.Inject

data class WmsState(
   val layer: Layer? = null,
   val baseUrl: String,
   val layers: List<String>,
   val boundingBox: BoundingBox? = null,
   val wmsCapabilities: WMSCapabilities,
   val credentials: Credentials? = null
) {
   val mapUrl = mapUrl()

   private fun mapUrl(): String {
      val format = wmsCapabilities.capability?.request?.map?.getImageFormat() ?: "image/png"
      val version = wmsCapabilities.version
      val transparent = format == "image/png"
      val epsg = if (version == "1.3" || version == "1.3.0") "CRS" else "SRS"

      return Uri.parse(baseUrl).buildUpon()
         .appendQueryParameter("REQUEST", "GetMap")
         .appendQueryParameter("SERVICE", "WMS")
         .appendQueryParameter("VERSION", version ?: "1.3.0")
         .appendQueryParameter(epsg, "EPSG:3857")
         .appendQueryParameter("WIDTH", "256")
         .appendQueryParameter("HEIGHT", "256")
         .appendQueryParameter("FORMAT", format)
         .appendQueryParameter("TRANSPARENT", transparent.toString())
         .appendQueryParameter("LAYERS", layers.joinToString(","))
         .appendQueryParameter("STYLES", "")
         .build()
         .toString()
   }
}

@HiltViewModel
class MapWMSLayerViewModel @Inject constructor(
   val layerService: LayerService,
   private val layerRepository: LayerRepository,
   private val preferencesRepository: SharedPreferencesRepository
): ViewModel() {
   private var wmsUrlJob: Job? = null
   private val _wmsState = MutableLiveData<WmsState?>()
   val wmsState: LiveData<WmsState?> = _wmsState

   private val _fetchError = MutableLiveData(false)
   val fetchError: LiveData<Boolean> = _fetchError

   private val _credentials = MutableLiveData<Credentials>()
   val credentials: LiveData<Credentials> = _credentials

   fun setLayerId(id: Long) {
      wmsUrlJob?.cancel()
      wmsUrlJob = viewModelScope.launch {
         delay(DEBOUNCE_TIMEOUT_MILLIS)

         layerRepository.getLayer(id)?.let { layer ->
            val uri = Uri.parse(layer.url)
            val baseUrl = "${uri.scheme}://${uri.host}/${uri.path}"
            val layers = uri.getQueryParameters("LAYERS")

            layerRepository.getWMSCapabilities(baseUrl)?.let { wmsCapabilities ->
               _wmsState.postValue(
                  WmsState(
                     layer = layer,
                     baseUrl = baseUrl,
                     layers = layers,
                     wmsCapabilities = wmsCapabilities,
                     credentials = credentials.value
                  )
               )
            }
         }
      }
   }

   fun setUrl(url: String, credentials: Credentials? = null) {
      viewModelScope.launch {
         val wmsCapabilities = layerRepository.getWMSCapabilities(url, credentials)
         if (wmsCapabilities != null) {
            _wmsState.postValue(
               WmsState(
                  baseUrl = url,
                  layers = emptyList(),
                  wmsCapabilities = wmsCapabilities,
                  credentials = credentials
               )
            )
            _fetchError.postValue(false)
         } else {
            _wmsState.postValue(null)
            _fetchError.postValue(true)
         }
      }
   }

   fun setLayer(layer: mil.nga.msi.network.layer.wms.Layer, name: String, enabled: Boolean) {
      wmsState.value?.let { wmsState ->
         val layerNames = wmsState.layers.toMutableSet().apply {
            if (enabled) add(name) else remove(name)
         }.toList()

         val builder = LatLngBounds.Builder()
         layerNames.mapNotNull {
            getLayer(it, layer)
         }.forEach {
            it.boundingBoxes.firstOrNull { boundingBox ->
               boundingBox.crs.equals("CRS:84", ignoreCase = true)
            }?.let { boundingBox ->
               val southwest = LatLng(boundingBox.minY, boundingBox.minX)
               val northeast = LatLng(boundingBox.maxY, boundingBox.maxX)
               builder.include(southwest)
               builder.include(northeast)
            }
         }

         val boundingBox = try {
            BoundingBox.fromLatLngBounds(builder.build())
         } catch (_: Exception) { null }

         _wmsState.value = wmsState.copy(
            layers = layerNames,
            boundingBox = boundingBox
         )
      }
   }

   fun setUsername(username: String) {
      _credentials.value = Credentials(
         username = username,
         password = _credentials.value?.password ?: ""
      )
   }

   fun setPassword(password: String) {
      _credentials.value = Credentials(
         username  =_credentials.value?.username ?: "",
         password
      )
   }

   suspend fun saveLayer(
      layer: Layer,
      credentials: Credentials?
   ) {
      if (layer.id == 0L) {
         val layerId = layerRepository.createLayer(layer)
         credentials?.let {
            preferencesRepository.setLayerCredentials(layerId, it)
         }
      } else {
         layerRepository.updateLayer(layer)
      }
   }

   private fun getLayer(
      name: String,
      layer: mil.nga.msi.network.layer.wms.Layer
   ): mil.nga.msi.network.layer.wms.Layer? {
      return if (layer.name == name) {
         layer
      } else if (layer.layers.isNotEmpty()) {
         layer.layers.first {
            getLayer(name, it) != null
         }
      } else null
   }

   companion object {
      private const val DEBOUNCE_TIMEOUT_MILLIS = 300L
   }
}