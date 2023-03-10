package mil.nga.msi.repository.layer

import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.network.layer.wms.WMSCapabilities
import javax.inject.Inject

class LayerRepository @Inject constructor(
   private val localDataSource: LayerLocalDataSource,
   private val remoteDataSource: LayerRemoteDataSource
) {
   fun observeLayers() = localDataSource.observeLayers()
   fun observeVisibleLayers() = localDataSource.observeVisibleLayers()

   suspend fun getTile(url: String) = remoteDataSource.getTile(url)
   suspend fun getWMSCapabilities(url: String) = remoteDataSource.getWMSCapabilities(url)

   suspend fun getLayer(id: Long) = localDataSource.getLayer(id)
   suspend fun createLayer(layer: Layer) = localDataSource.insertLayer(layer)
   suspend fun updateLayer(layer: Layer) = localDataSource.updateLayer(layer)
   suspend fun enabledLayer(layer: Layer, enabled: Boolean) = localDataSource.enableLayer(layer, enabled)
   suspend fun deleteLayer(layer: Layer) = localDataSource.deleteLayer(layer)
}