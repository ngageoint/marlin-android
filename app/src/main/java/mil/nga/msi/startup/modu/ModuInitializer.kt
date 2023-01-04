package mil.nga.msi.startup.modu

import android.content.Context
import androidx.startup.Initializer
import androidx.work.*
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.modu.LoadModuWorker
import mil.nga.msi.work.modu.RefreshModuWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ModuInitializer: Initializer<Unit> {
   @Inject lateinit var workManager: WorkManager

   override fun create(context: Context) {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      fetchModus()
      fetchModusPeriodically()
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }

   private fun fetchModus() {
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

   private fun fetchModusPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshModuWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      )

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_MODUS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      const val FETCH_LATEST_MODUS_TASK = "FetchLatestModuTask"
   }
}