package mil.nga.msi.repository.layer

import mil.nga.msi.datasource.layer.Layer
import mil.nga.msi.datasource.layer.LayerDao
import javax.inject.Inject

class LayerLocalDataSource @Inject constructor(
   private val dao: LayerDao
) {
   suspend fun insert(layer: Layer) = dao.insert(layer)
   suspend fun delete(layer: Layer) = dao.delete(layer)
   suspend fun enable(layer: Layer, enabled: Boolean) = dao.enable(layer.id, enabled)

   fun observeLayers() = dao.observeLayers()
   fun observeVisibleLayers() = dao.observeVisibleLayers()
}