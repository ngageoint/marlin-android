package mil.nga.msi.repository.port

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortDao
import mil.nga.msi.datasource.port.PortListItem
import javax.inject.Inject

class PortLocalDataSource @Inject constructor(
   private val dao: PortDao
) {
   fun observePort(portNumber: Int) = dao.observePort(portNumber)
   fun observePortMapItems() = dao.observePortMapItems()
   fun observePortListItems(query: SimpleSQLiteQuery): PagingSource<Int, PortListItem> = dao.observePortListItems(query)

   fun getPorts(query: SimpleSQLiteQuery) = dao.getPorts(query)

   fun isEmpty() = dao.count() == 0

   suspend fun getPort(portNumber: Int) = dao.getPort(portNumber)
   suspend fun getPorts(): List<Port> = dao.getPorts()
   suspend fun existingPorts(portNumbers: List<Int>) = dao.existingPorts(portNumbers)

   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun insert(ports: List<Port>) = dao.insert(ports)
}