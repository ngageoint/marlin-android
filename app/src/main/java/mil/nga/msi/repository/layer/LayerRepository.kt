package mil.nga.msi.repository.layer

import mil.nga.msi.datasource.layer.Layer
import javax.inject.Inject

class LayerRepository @Inject constructor(
   private val localDataSource: LayerLocalDataSource,
   private val remoteDataSource: LayerRemoteDataSource
) {
   fun observeLayers() = localDataSource.observeLayers()
   fun observeVisibleLayers() = localDataSource.observeVisibleLayers()

   suspend fun getTile(url: String): Boolean {
      return remoteDataSource.getTile(url)
   }

   suspend fun createLayer(layer: Layer) {
      localDataSource.insert(layer)
   }

   suspend fun enabledLayer(layer: Layer, enabled: Boolean) {
      localDataSource.enable(layer, enabled)
   }

   suspend fun deleteLayer(layer: Layer) {
      localDataSource.delete(layer)
   }
}