package mil.nga.msi.repository.modu

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuListItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.filter.Filter
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
   fun observeModu(name: String) = localDataSource.observeModu(name)
   suspend fun getModu(name: String) = localDataSource.getModu(name)

   @OptIn(ExperimentalCoroutinesApi::class)
   fun observeModuMapItems(): Flow<List<ModuMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.MODU] ?: emptyList()
         val query = QueryBuilder("modus", filters).buildQuery()
         localDataSource.observeModuMapItems(query)
      }
   }

   fun observeModuListItems(filters: List<Filter>): PagingSource<Int, ModuListItem> {
      val query = QueryBuilder("modus", filters).buildQuery()
      return localDataSource.observeModuListItems(query)
   }

   fun getModus(filters: List<Filter>): List<Modu> {
      val query = QueryBuilder("modus", filters).buildQuery()
      return localDataSource.getModus(query)
   }

   suspend fun fetchModus(refresh: Boolean = false): List<Modu> {
      if (refresh) {
         val modus = remoteDataSource.fetchModus()

         val fetched = userPreferencesRepository.fetched(DataSource.MODU)
         if (fetched == null) {
            val newModus = modus.subtract(localDataSource.existingModus(modus.map { it.name }).toSet()).toList()
            notification.modu(newModus)
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