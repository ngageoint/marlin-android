package mil.nga.msi.repository.asam

import android.app.Application
import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.asam.LoadAsamWorker
import mil.nga.msi.work.asam.RefreshAsamWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsamRepository @Inject constructor(
   val application: Application,
   private val workManager: WorkManager,
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

   fun observeAsamListItems(filters: List<Filter>): PagingSource<Int, AsamListItem> {
      val query = QueryBuilder("asams", filters).buildQuery()
      return localDataSource.observeAsamListItems(query)
   }

   suspend fun getAsams(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Asam>  {
      val filters = filterRepository.filters.first()[DataSource.ASAM] ?: emptyList()

      val filtersWithBounds = filters.toMutableList().apply {
         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Min Latitude",
                  parameter =  "latitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLatitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Min Longitude",
                  parameter =  "longitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLongitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Max Latitude",
                  parameter =  "latitude",
               ),
               comparator = ComparatorType.LESS_THAN_OR_EQUAL,
               value = maxLatitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Max Longitude",
                  parameter =  "longitude",
               ),
               comparator = ComparatorType.LESS_THAN_OR_EQUAL,
               value = maxLongitude
            )
         )
      }

      val query = QueryBuilder("asams", filtersWithBounds).buildQuery()
      return localDataSource.getAsams(query)
   }

   suspend fun fetchAsams(refresh: Boolean = false): List<Asam> {
      if (refresh) {
         val asams = remoteDataSource.fetchAsams()

         val fetched = userPreferencesRepository.fetched(DataSource.ASAM)
         if (fetched != null) {
            val newAsams = asams.subtract(localDataSource.existingAsams(asams.map { it.reference }).toSet()).toList()
            notification.asam(newAsams)
         }

         localDataSource.insert(asams)
      }

      return localDataSource.getAsams()
   }

   fun fetchAsams() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadAsamWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshAsamWorker::class.java)
         .setConstraints(
            Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_ASAMS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   fun fetchAsamsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshAsamWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_ASAMS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_ASAMS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_ASAMS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_ASAMS_TASK = "FetchLatestAsamsTask"
      private const val TAG_FETCH_LATEST_ASAMS = "FetchLatestAsamsTaskTag"
   }
}