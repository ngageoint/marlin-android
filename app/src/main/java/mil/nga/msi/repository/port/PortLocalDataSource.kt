package mil.nga.msi.repository.port

import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortDao
import javax.inject.Inject

class PortLocalDataSource @Inject constructor(
   private val dao: PortDao
) {
   fun observePort(portNumber: Int) = dao.observePort(portNumber)
   fun observePortListItems() = dao.getPortListItems()

   suspend fun getPort(portNumber: Int) = dao.getPort(portNumber)
   suspend fun getPorts(): List<Port> = dao.getPorts()

   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun insert(ports: List<Port>) = dao.insert(ports)
}