package mil.nga.msi.ui.port.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.PortListItem
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class PortsViewModel @Inject constructor(
   locationPolicy: LocationPolicy,
   filterRepository: FilterRepository,
   private val repository: PortRepository
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   @OptIn(ExperimentalCoroutinesApi::class)
   val ports: Flow<PagingData<PortListItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.PORT] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observePortListItems(filters)
      }.flow
   }

   val portFilters = filterRepository.filters.map { entry ->
      entry[DataSource.PORT] ?: emptyList()
   }.asLiveData()

   suspend fun getPort(portNumber: Int) = repository.getPort(portNumber)
}