package mil.nga.msi.repository.asam

import androidx.work.*
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.work.RefreshAsamWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsamRepository @Inject constructor(
   private val workManager: WorkManager,
   private val asamLocalDataSource: AsamLocalDataSource,
   private val asamRemoteDataSource: AsamRemoteDataSource
) {
   suspend fun getAsams(refresh: Boolean = false): List<Asam> {
      if (refresh) {
         val asams = asamRemoteDataSource.fetchAsams()
         asamLocalDataSource.insert(asams)
      }

      return asamLocalDataSource.getAsams()
   }

   fun fetchAsams() {
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
         .enqueueUniqueWork(
            FETCH_LATEST_ASAMS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchAsamsPeriodically() {
      val fetchNewsRequest = PeriodicWorkRequestBuilder<RefreshAsamWorker>(
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
         fetchNewsRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_ASAMS_TASK = "FetchLatestAsamsTask"
      private const val TAG_FETCH_LATEST_ASAMS = "FetchLatestAsamsTaskTag"
   }
}