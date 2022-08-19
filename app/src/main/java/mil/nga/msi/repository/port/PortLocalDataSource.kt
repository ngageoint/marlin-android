package mil.nga.msi.repository.port

import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortDao
import javax.inject.Inject

class PortLocalDataSource @Inject constructor(
   private val dao: PortDao
) {
//   fun observeAsams() = dao.observeAsams()
//   fun observeAsam(reference: String) = dao.observeAsam(reference)
//   fun observeAsamMapItems() = dao.observeAsamMapItems()
//   fun observeAsamListItems() = dao.getAsamListItems()
//
//   suspend fun getAsam(reference: String) = dao.getAsam(reference)
   suspend fun getPorts(): List<Port> = dao.getPorts()
//   suspend fun getLatestAsam() = dao.getLatestAsam()

   suspend fun insert(ports: List<Port>) = dao.insert(ports)
}