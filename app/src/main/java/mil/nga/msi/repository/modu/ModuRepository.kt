package mil.nga.msi.repository.modu

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.*
import kotlinx.coroutines.flow.first
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuListItem
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.modu.LoadModuWorker
import mil.nga.msi.work.modu.RefreshModuWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ModuRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: ModuLocalDataSource,
   private val remoteDataSource: ModuRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   val modus = localDataSource.observeModus()
   val moduMapItems = localDataSource.observeModuMapItems()
   fun observeModu(name: String) = localDataSource.observeModu(name)
   suspend fun getModu(name: String) = localDataSource.getModu(name)

   fun observeModuListItems(filters: List<Filter>): PagingSource<Int, ModuListItem> {
      val query = QueryBuilder("modus", filters).buildQuery()
      return localDataSource.observeModuListItems(query)
   }

   suspend fun getModus(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Modu>  {
      val filters = filterRepository.filters.first()[DataSource.MODU] ?: emptyList()

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

      val query = QueryBuilder("modus", filtersWithBounds).buildQuery()
      return localDataSource.getModus(query)
   }

   suspend fun fetchModus(refresh: Boolean = false): List<Modu> {
      if (refresh) {
         val modus = remoteDataSource.fetchModus()

         val fetched = userPreferencesRepository.fetched(DataSource.MODU)
         if (fetched == null) {
            val newModus = modus.subtract(localDataSource.existingModus(modus.map { it.name }).toSet()).toList()
            notification.modo(newModus)
         }

         localDataSource.insert(modus)
      }

      return localDataSource.getModus()
   }

   fun fetchModus() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadModuWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshModuWorker::class.java)
         .setConstraints(
            Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_MODUS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   fun fetchModusPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshModuWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_MODUS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_MODUS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_MODUS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_MODUS_TASK = "FetchLatestModuTask"
      private const val TAG_FETCH_LATEST_MODUS = "FetchLatestModuTaskTag"
   }
}