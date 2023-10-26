package mil.nga.msi.repository.port

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortDao
import javax.inject.Inject

class PortLocalDataSource @Inject constructor(
   private val dao: PortDao
) {
   fun observePort(portNumber: Int) = dao.observePort(portNumber)
   fun observePortMapItems(query: SimpleSQLiteQuery) = dao.observePortMapItems(query)
   fun observePortListItems(query: SimpleSQLiteQuery) = dao.observePortListItems(query)

   fun getPorts(query: SimpleSQLiteQuery) = dao.getPorts(query)

   fun isEmpty() = dao.count() == 0
   suspend fun count(query: SimpleSQLiteQuery) = dao.count(query)

   suspend fun getPort(portNumber: Int) = dao.getPort(portNumber)
   suspend fun getPorts(): List<Port> = dao.getPorts()
   suspend fun existingPorts(portNumbers: List<Int>) = dao.getPorts(portNumbers)

   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun insert(ports: List<Port>) = dao.insert(ports)
}