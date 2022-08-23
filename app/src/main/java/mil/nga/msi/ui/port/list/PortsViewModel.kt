package mil.nga.msi.ui.port.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.port.PortRepository
import javax.inject.Inject

@HiltViewModel
class PortsViewModel @Inject constructor(
   private val repository: PortRepository,
   locationPolicy: LocationPolicy
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   suspend fun getPort(portNumber: Int): Port? {
      return repository.getPort(portNumber)
   }

   suspend fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
   ) = withContext(Dispatchers.IO) {
      repository.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude)
   }

   val ports = Pager(PagingConfig(pageSize = 20), null) {
      repository.getPortListItems()
   }.flow
}