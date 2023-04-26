package mil.nga.msi.ui.map.settings.layers.geopackage

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.GeoPackageManager
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlay
import mil.nga.geopackage.map.tiles.overlay.XYZGeoPackageOverlay
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles
import mil.nga.msi.datasource.layer.BoundingBox
import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerType
import mil.nga.msi.geopackage.latLngBounds
import mil.nga.msi.repository.layer.LayerRepository
import java.io.File
import javax.inject.Inject

data class GeoPackageOverlay(
   val table: String,
   val tileProvider: TileProvider,
   val boundingBox: LatLngBounds
)

data class GeoPackageState(
   val layer: Layer? = null,
   val geoPackage: GeoPackage,
   val overlays: List<GeoPackageOverlay>,
   val boundingBox: BoundingBox? = null,
   val selectedLayers: List<String> = emptyList(),
   val tileProviders: List<TileProvider> = emptyList()
)

@HiltViewModel
class GeopackageLayerViewModel @Inject constructor(
   private val application: Application,
   private val layerRepository: LayerRepository,
   private val geoPackageManager: GeoPackageManager
): ViewModel() {
   private val _geopackageState = MutableLiveData<GeoPackageState>()
   val geopackageState: LiveData<GeoPackageState> = _geopackageState

   suspend fun getGeoPackage(uri: Uri): File? = withContext(Dispatchers.IO) {
      val file = layerRepository.stageGeoPackageFile(uri)
      try {
         geoPackageManager.openExternal(file)
         file
      } catch (e: Exception) { null }
   }

   fun setLayerId(id: Long) {
      viewModelScope.launch {
         layerRepository.getLayer(id)?.let { layer ->
            try {
               val geoPackage = geoPackageManager.openExternal(layer.filePath)
               _geopackageState.postValue(
                  GeoPackageState(
                     layer = layer,
                     geoPackage = geoPackage,
                     overlays = geoPackage.getOverlays(),
                     selectedLayers = layer.url.split(",")
                  )
               )
            } catch (_: Exception) { }
         }
      }
   }

   fun setUri(uri: Uri) {
      viewModelScope.launch(Dispatchers.IO) {
         val file = layerRepository.stageGeoPackageFile(uri)
         try {
            val geoPackage = geoPackageManager.openExternal(file)
            _geopackageState.postValue(
               GeoPackageState(
                  layer = Layer(
                     id = 0,
                     type = LayerType.GEOPACKAGE,
                     name = "",
                     url = "",
                     filePath = file.absolutePath
                  ),
                  geoPackage = geoPackage,
                  overlays = geoPackage.getOverlays()
               )
            )
         } catch (_: Exception) { }
      }
   }

   fun enableLayer(layer: Layer) {
      viewModelScope.launch(Dispatchers.IO) {
         val geoPackage = geoPackageManager.openExternal(layer.filePath)
         _geopackageState.postValue(
            GeoPackageState(
               layer = layer,
               geoPackage = geoPackage,
               overlays = geoPackage.getOverlays()
            )
         )
      }
   }

   fun enableLayer(layer: String, enabled: Boolean) {
      geopackageState.value?.let { state ->
         val layers = state.selectedLayers.toMutableSet().apply {
            if (enabled) add(layer) else remove(layer)
         }.toList()

         val builder = LatLngBounds.Builder()
         state.geoPackage.tileTables.forEach { table ->
            val bounds = state.geoPackage.getTileDao(table).latLngBounds()
            builder.include(bounds.southwest)
            builder.include(bounds.northeast)
         }

         state.geoPackage.featureTables.forEach { table ->
            val bounds = state.geoPackage.getFeatureDao(table).latLngBounds()
            builder.include(bounds.southwest)
            builder.include(bounds.northeast)
         }

         val boundingBox = try {
            BoundingBox.fromLatLngBounds(builder.build())
         } catch (_: Exception) { null }

         _geopackageState.value = state.copy(
            selectedLayers = layers,
            boundingBox = boundingBox
         )
      }
   }

   suspend fun saveLayer(layer: Layer) {
      if (layer.id == 0L) {
         layerRepository.createLayer(layer)
      } else {
         layerRepository.updateLayer(layer)
      }
   }

   private fun GeoPackage.getOverlays(): List<GeoPackageOverlay> {
      val tiles = tileTables.map { table ->
         val tileDao = getTileDao(table)
         val tileProvider = XYZGeoPackageOverlay(tileDao)

         GeoPackageOverlay(
            table = table,
            tileProvider = tileProvider,
            boundingBox = tileDao.latLngBounds()
         )
      }

      val features = featureTables.map { table ->
         val featureDao = getFeatureDao(table)
         val featureTiles = DefaultFeatureTiles(application, this, featureDao)
         val tileProvider = FeatureOverlay(featureTiles)

         GeoPackageOverlay(
            table = table,
            tileProvider = tileProvider,
            boundingBox = featureDao.latLngBounds()
         )
      }

      return tiles + features
   }
}