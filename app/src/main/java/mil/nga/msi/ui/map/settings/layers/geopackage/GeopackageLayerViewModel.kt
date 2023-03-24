package mil.nga.msi.ui.map.settings.layers.geopackage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.repository.layer.LayerRepository
import java.io.File
import javax.inject.Inject

enum class GeoPackageLayerType { TILE, FEATURE }

data class GeoPackageLayer(
   val table: String,
   val type: GeoPackageLayerType
)

data class GeoPackageState(
   val layer: Layer? = null,
   val geoPackage: GeoPackage,
   val layers: List<GeoPackageLayer>,
   val selectedLayers: List<String> = emptyList(),
   val tileProviders: List<TileProvider> = emptyList()
)

@HiltViewModel
class GeopackageLayerViewModel @Inject constructor(
   private val layerRepository: LayerRepository,
   private val geoPackageManager: GeoPackageManager
): ViewModel() {
   private val _geopackageState = MutableLiveData<GeoPackageState>()
   val geopackageState: LiveData<GeoPackageState> = _geopackageState

   suspend fun setUri(uri: Uri): File? = withContext(Dispatchers.IO) {
      val file = layerRepository.stageGeoPackageFile(uri)
      try {
         geoPackageManager.openExternal(file)
         file
      } catch (e: Exception) { null }
   }

   fun setLayer(layer: Layer) {
      viewModelScope.launch(Dispatchers.IO) {
         val geoPackage = geoPackageManager.openExternal(layer.filePath)
         val layers =
            geoPackage.tileTables.map { GeoPackageLayer(table = it, type = GeoPackageLayerType.TILE) } +
            geoPackage.featureTables.map { GeoPackageLayer(table = it, type = GeoPackageLayerType.FEATURE) }

         _geopackageState.postValue(
            GeoPackageState(
               layer = layer,
               geoPackage = geoPackage,
               layers = layers.sortedBy { it.table }
            )
         )
      }
   }

   fun setLayer(layer: String, enabled: Boolean) {
      geopackageState.value?.let { state ->
         val layers = state.selectedLayers.toMutableSet().apply {
            if (enabled) add(layer) else remove(layer)
         }.toList()

         _geopackageState.value = state.copy(selectedLayers = layers)
      }
   }

   suspend fun saveLayer(layer: Layer) {
      if (layer.id == 0L) {
         layerRepository.createLayer(layer)
      } else {
         layerRepository.updateLayer(layer)
      }
   }
}