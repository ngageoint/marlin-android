package mil.nga.msi.repository.light

import androidx.lifecycle.map
import androidx.work.*
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.work.light.RefreshLightWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LightRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: LightLocalDataSource,
   private val remoteDataSource: LightRemoteDataSource,
   private val notification: MarlinNotification
) {
   val lightMapItems = localDataSource.observeLightMapItems()
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

   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude)

   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
      characteristicNumber: Int
   ) = localDataSource.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber)

   suspend fun fetchLights(refresh: Boolean = false): List<Light> {
      if (refresh) {
         val newLights = mutableListOf<Light>()
         val isEmpty = localDataSource.isEmpty()

         PublicationVolume.values().forEach { volume ->
            val lights = remoteDataSource.fetchLights(volume)

            if (!isEmpty) {
               newLights.addAll(lights.subtract(localDataSource.existingLights(lights.map { it.compositeKey() }).toSet()).toList())
            }

            localDataSource.insert(lights)
         }

         notification.light(newLights)
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

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_LIGHTS_TASK).map { workInfo ->
      workInfo.first()?.state == WorkInfo.State.RUNNING
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_LIGHTS_TASK = "FetchLatestLightsTask"
      private const val TAG_FETCH_LATEST_LIGHTS = "FetchLatestLightsTaskTag"
   }
}