package mil.nga.msi.startup.navigationalwarning

import android.content.Context
import androidx.startup.Initializer
import androidx.work.*
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.navigationalwarning.RefreshNavigationalWarningWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NavigationalWarningInitializer: Initializer<Unit> {
   @Inject lateinit var workManager: WorkManager

   override fun create(context: Context) {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      fetchNavigationalWarnings()
      fetchNavigationalWarningsPeriodically()
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }

   private fun fetchNavigationalWarnings() {
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

   private fun fetchNavigationalWarningsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshNavigationalWarningWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      )

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      const val FETCH_LATEST_NAVIGATIONAL_WARNINGS_TASK = "FetchLatestNavigationalWarningsTask"
   }
}