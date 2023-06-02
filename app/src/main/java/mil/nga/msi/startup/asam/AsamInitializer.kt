package mil.nga.msi.startup.asam

import android.content.Context
import androidx.startup.Initializer
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.asam.LoadAsamWorker
import mil.nga.msi.work.asam.RefreshAsamWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AsamInitializer: Initializer<Unit> {
   @Inject lateinit var workManager: WorkManager

   override fun create(context: Context) {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      fetchAsams()
      fetchAsamsPeriodically()
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }

   private fun fetchAsams() {
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

   private fun fetchAsamsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshAsamWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      )

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_ASAMS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      const val FETCH_LATEST_ASAMS_TASK = "FetchLatestAsamsTask"
   }
}