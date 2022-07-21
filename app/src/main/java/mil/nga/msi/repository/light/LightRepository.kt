package mil.nga.msi.repository.light

import android.util.Log
import androidx.work.*
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightVolume
import mil.nga.msi.work.light.RefreshLightWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LightRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: LightLocalDataSource,
   private val remoteDataSource: LightRemoteDataSource
) {
//   val asams = localDataSource.observeAsams()
//   val asamMapItems = localDataSource.observeAsamMapItems()
//   fun getAsamListItems() = localDataSource.observeAsamListItems()

//   fun observeAsam(reference: String) = localDataSource.observeAsam(reference)
//   suspend fun getAsam(reference: String) = localDataSource.getAsam(reference)

   suspend fun fetchLights(refresh: Boolean = false): List<Light> {
      if (refresh) {
         LightVolume.values().forEach { lightVolume ->
            Log.i("Billy", "Fetching lights for volume $lightVolume")
            val lights = remoteDataSource.fetchLights(lightVolume)
            Log.i("Billy", "Done fetching lights for volume $lightVolume")

            localDataSource.insert(lights)
            Log.i("Billy", "Done inserting lights for volume $lightVolume")

         }
      }

      return localDataSource.getLights()
   }

   fun fetchLights() {
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshLightWorker::class.java)
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
            FETCH_LATEST_LIGHTS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchLightsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshLightWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_LIGHTS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_LIGHTS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_LIGHTS_TASK = "FetchLatestLightsTask"
      private const val TAG_FETCH_LATEST_LIGHTS = "FetchLatestLightsTaskTag"
   }
}