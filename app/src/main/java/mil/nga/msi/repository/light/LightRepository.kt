package mil.nga.msi.repository.light

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
   val mapItems = localDataSource.observeMapItems()
   fun getLightListItems() = localDataSource.observeLightListItems()

   fun observeLight(
      volumeNumber: String,
      featureNumber: String,
   ) = localDataSource.observeLight(volumeNumber, featureNumber)

   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ) = localDataSource.getLight(volumeNumber, featureNumber, characteristicNumber)

   suspend fun fetchLights(refresh: Boolean = false): List<Light> {
      if (refresh) {
         LightVolume.values().forEach { lightVolume ->
            val lights = remoteDataSource.fetchLights(lightVolume)
            localDataSource.insert(lights)
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