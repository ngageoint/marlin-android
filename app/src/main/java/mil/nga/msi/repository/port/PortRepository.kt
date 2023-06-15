package mil.nga.msi.repository.port

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.port.PortMapItem
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.startup.port.PortInitializer.Companion.FETCH_LATEST_PORTS_TASK
import javax.inject.Inject

class PortRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: PortLocalDataSource,
   private val remoteDataSource: PortRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {

   @OptIn(ExperimentalCoroutinesApi::class)
   fun observePortMapItems(): Flow<List<PortMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.PORT] ?: emptyList()
         val query = QueryBuilder("ports", filters).buildQuery()
         localDataSource.observePortMapItems(query)
      }
   }

   fun observePortListItems(filters: List<Filter>, sort: List<SortParameter>): PagingSource<Int, Port> {
      val query = QueryBuilder(
         table = "ports",
         filters = filters,
         sort = sort
      ).buildQuery()

      return localDataSource.observePortListItems(query)
   }

   fun getPorts(filters: List<Filter>): List<Port> {
      val query = QueryBuilder("ports", filters).buildQuery()
      return localDataSource.getPorts(query)
   }

   fun observePort(portNumber: Int) = localDataSource.observePort(portNumber)
   suspend fun getPort(portNumber: Int) = localDataSource.getPort(portNumber)

   suspend fun fetchPorts(refresh: Boolean = false): List<Port> {
      if (refresh) {
         val ports = remoteDataSource.fetchPorts()

         val fetched = userPreferencesRepository.fetched(DataSource.PORT)
         if (fetched != null) {
            val newPorts = ports.subtract(localDataSource.existingPorts(ports.map { it.portNumber }).toSet()).toList()
            notification.port(newPorts)
         }

         localDataSource.insert(ports)
      }

      return localDataSource.getPorts()
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_PORTS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}