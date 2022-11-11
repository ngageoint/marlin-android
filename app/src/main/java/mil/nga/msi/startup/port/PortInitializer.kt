package mil.nga.msi.startup.port

import android.content.Context
import androidx.startup.Initializer
import androidx.work.*
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.port.LoadPortWorker
import mil.nga.msi.work.port.RefreshPortWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PortInitializer: Initializer<Unit> {
   @Inject lateinit var workManager: WorkManager

   override fun create(context: Context) {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      fetchPorts()
      fetchPortsPeriodically()
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }

   private fun fetchPorts() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadPortWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshPortWorker::class.java)
         .setConstraints(
            Constraints.Builder()
               .setRequiredNetworkType(NetworkType.CONNECTED)
               .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_PORTS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   private fun fetchPortsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshPortWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      )

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_PORTS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      const val FETCH_LATEST_PORTS_TASK = "FetchLatestPortsTask"
   }
}