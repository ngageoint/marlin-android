package mil.nga.msi.repository.navigationalwarning

import androidx.work.*
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.work.navigationalwarning.RefreshNavigationalWarningWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NavigationalWarningRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: NavigationalWarningLocalDataSource,
   private val remoteDataSource: NavigationalWarningRemoteDataSource
) {
   fun getNavigationalWarningListItems() = localDataSource.observeNavigationalWarningListItems()

   suspend fun getNavigationalWarning(number: Int) = localDataSource.getNavigationalWarning(number)

   suspend fun fetchNavigationalWarnings(refresh: Boolean = false): List<NavigationalWarning> {
      if (refresh) {
         val warnings = remoteDataSource.fetchNavigationalWarnings()
         localDataSource.insert(warnings)
      }

      return localDataSource.getNavigationalWarnings()
   }

   fun fetchNavigationalWarnings() {
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshNavigationalWarningWorker::class.java)
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
            FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchNavigaionalWarningsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshNavigationalWarningWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_NAVIGATIONAL_WARNINGS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK = "FetchLatestNavigationalWarningsTask"
      private const val TAG_FETCH_LATEST_NAVIGATIONAL_WARNINGS = "FetchLatestNavigationalWarningTaskTag"
   }
}