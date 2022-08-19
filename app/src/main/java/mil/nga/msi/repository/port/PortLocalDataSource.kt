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
   fun observePortListItems() = dao.getPortListItems()

   suspend fun getPort(portNumber: Int) = dao.getPort(portNumber)
   suspend fun getPorts(): List<Port> = dao.getPorts()
//   suspend fun getLatestAsam() = dao.getLatestAsam()

   suspend fun insert(ports: List<Port>) = dao.insert(ports)
}