package mil.nga.msi.repository.modu

import androidx.lifecycle.map
import androidx.work.*
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
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
   private val userPreferencesRepository: UserPreferencesRepository
) {
   val modus = localDataSource.observeModus()
   val moduMapItems = localDataSource.observeModuMapItems()
   fun getModuListItems() = localDataSource.observeModuListItems()

   fun observeModu(name: String) = localDataSource.observeModu(name)
   suspend fun getModu(name: String) = localDataSource.getModu(name)

   fun getModus(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getModus(minLatitude, maxLatitude, minLongitude, maxLongitude)

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
      workInfo.first()?.state == WorkInfo.State.RUNNING
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_MODUS_TASK = "FetchLatestModuTask"
      private const val TAG_FETCH_LATEST_MODUS = "FetchLatestModuTaskTag"
   }
}