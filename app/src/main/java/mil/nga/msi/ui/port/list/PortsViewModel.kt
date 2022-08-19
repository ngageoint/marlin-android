package mil.nga.msi.ui.port.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.port.PortRepository
import javax.inject.Inject

@HiltViewModel
class PortsViewModel @Inject constructor(
   private val repository: PortRepository,
   private val locationPolicy: LocationPolicy
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   suspend fun getPort(portNumber: Int): Port? {
      return repository.getPort(portNumber)
   }

   val ports = Pager(PagingConfig(pageSize = 20), null) {
      repository.getPortListItems()
   }.flow
}