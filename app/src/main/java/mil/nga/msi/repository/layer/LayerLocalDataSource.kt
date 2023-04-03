package mil.nga.msi.repository.layer

import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerDao
import javax.inject.Inject

class LayerLocalDataSource @Inject constructor(
   private val dao: LayerDao
) {
   suspend fun insertLayer(layer: Layer) = dao.insert(layer)
   suspend fun updateLayer(layer: Layer) = dao.update(layer)
   suspend fun deleteLayer(layer: Layer) = dao.delete(layer)
   suspend fun getLayer(id: Long) = dao.getLayer(id)
   suspend fun enableLayer(layer: Layer, enabled: Boolean) = dao.enable(layer.id, enabled)

   fun observeLayers() = dao.observeLayers()
   fun observeVisibleLayers() = dao.observeVisibleLayers()
}