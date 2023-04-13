package mil.nga.msi.repository.asam

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.startup.asam.AsamInitializer.Companion.FETCH_LATEST_ASAMS_TASK
import javax.inject.Inject

class AsamRepository @Inject constructor(
   workManager: WorkManager,
   private val localDataSource: AsamLocalDataSource,
   private val remoteDataSource: AsamRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   val asams = localDataSource.observeAsams()

   fun observeAsam(reference: String) = localDataSource.observeAsam(reference)
   suspend fun getAsam(reference: String) = localDataSource.getAsam(reference)

   @OptIn(ExperimentalCoroutinesApi::class)
   fun observeAsamMapItems(): Flow<List<AsamMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.ASAM] ?: emptyList()
         val query = QueryBuilder("asams", filters).buildQuery()
         localDataSource.observeAsamMapItems(query)
      }
   }

   fun observeAsamListItems(filters: List<Filter>, sort: List<SortParameter>): PagingSource<Int, Asam> {
      val query = QueryBuilder(
         table = "asams",
         filters = filters,
         sort = sort
      ).buildQuery()

      return localDataSource.observeAsamListItems(query)
   }

   fun getAsams(filters: List<Filter>): List<Asam> {
      val query = QueryBuilder(
         table = "asams",
         filters = filters
      ).buildQuery()
      return localDataSource.getAsams(query)
   }

   suspend fun fetchAsams(refresh: Boolean = false): List<Asam> {
      if (refresh) {
         val msiAsam = remoteDataSource.fetchAsams()

         val fetched = userPreferencesRepository.fetched(DataSource.ASAM)
         if (fetched != null) {
            val newAsams = msiAsam.asams.subtract(localDataSource.existingAsams(msiAsam.asams.map { it.reference }).toSet()).toList()
            notification.asam(newAsams)
         }

         localDataSource.insert(msiAsam.asams)
      }

      return localDataSource.getAsams()
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_ASAMS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}