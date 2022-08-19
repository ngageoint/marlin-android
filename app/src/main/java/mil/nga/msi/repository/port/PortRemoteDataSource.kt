package mil.nga.msi.repository.port

import mil.nga.msi.datasource.port.Port
import mil.nga.msi.network.port.PortService
import javax.inject.Inject

class PortRemoteDataSource @Inject constructor(
   private val service: PortService
) {
   suspend fun fetchPorts(): List<Port> {
      val ports = mutableListOf<Port>()

      val response = service.getPorts()
      if (response.isSuccessful) {
         response.body()?.let {
            ports.addAll(it)
         }
      }

      return ports
   }
}