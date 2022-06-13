package mil.nga.msi.repository.modu

import androidx.work.*
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.work.modu.RefreshModuWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ModuRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: ModuLocalDataSource,
   private val remoteDataSource: ModuRemoteDataSource
) {
   val modus = localDataSource.observeModus()
   val moduMapItems = localDataSource.observeModuMapItems()
   fun getModuListItems() = localDataSource.observeModuListItems()

   fun observeModu(name: String) = localDataSource.observeModu(name)
   suspend fun getModu(name: String) = localDataSource.getModu(name)

   suspend fun fetchModus(refresh: Boolean = false): List<Modu> {
      if (refresh) {
         val modus = remoteDataSource.fetchModus()
         localDataSource.insert(modus)
      }

      return localDataSource.getModus()
   }

   fun fetchModus() {
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
         .enqueueUniqueWork(
            FETCH_LATEST_MODUS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchModusPeriodically() {
      val fetchNewsRequest = PeriodicWorkRequestBuilder<RefreshModuWorker>(
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
         fetchNewsRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_MODUS_TASK = "FetchLatestModuTask"
      private const val TAG_FETCH_LATEST_MODUS = "FetchLatestModuTaskTag"
   }
}