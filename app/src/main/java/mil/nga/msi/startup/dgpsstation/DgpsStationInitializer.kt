package mil.nga.msi.startup.dgpsstation

import android.content.Context
import androidx.startup.Initializer
import androidx.work.*
import mil.nga.msi.di.AppInitializer
import mil.nga.msi.startup.WorkManagerInitializer
import mil.nga.msi.work.dgpsstation.LoadDgpsStationWorker
import mil.nga.msi.work.dgpsstation.RefreshDgpsStationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DgpsStationInitializer: Initializer<Unit> {
   @Inject lateinit var workManager: WorkManager

   override fun create(context: Context) {
      // Inject Hilt dependencies
      AppInitializer.resolve(context).inject(this)

      fetchDgpsStations()
      fetchDgpsStationsPeriodically()
   }

   override fun dependencies(): List<Class<out Initializer<*>>> {
      return listOf(WorkManagerInitializer::class.java)
   }

   private fun fetchDgpsStations() {
      val loadRequest = OneTimeWorkRequest.Builder(LoadDgpsStationWorker::class.java).build()
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshDgpsStationWorker::class.java)
         .setConstraints(
            Constraints.Builder()
               .setRequiredNetworkType(NetworkType.CONNECTED)
               .build()
         )
         .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
         .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
         .build()

      workManager
         .beginUniqueWork(FETCH_LATEST_DGPS_STATIONS_TASK, ExistingWorkPolicy.KEEP, loadRequest)
         .then(fetchRequest)
         .enqueue()
   }

   private fun fetchDgpsStationsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshDgpsStationWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      )

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_DGPS_STATIONS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      const val FETCH_LATEST_DGPS_STATIONS_TASK = "FetchLatestDgpsStationsTask"
   }
}