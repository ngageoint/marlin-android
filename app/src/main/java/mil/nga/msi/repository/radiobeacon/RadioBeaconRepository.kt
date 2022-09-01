package mil.nga.msi.repository.radiobeacon

import androidx.work.*
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.work.radiobeacon.RefreshRadioBeaconWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RadioBeaconRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: RadioBeaconLocalDataSource,
   private val remoteDataSource: RadioBeaconRemoteDataSource
) {
   fun getRadioBeaconListItems() = localDataSource.observeRadioBeaconListItems()

   fun observeRadioBeacon(
      volumeNumber: String,
      featureNumber: String,
   ) = localDataSource.observeRadioBeacon(volumeNumber, featureNumber)

   suspend fun getRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ) = localDataSource.getRadioBeacon(volumeNumber, featureNumber)

   fun getRadioBeacons(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun fetchRadioBeacons(refresh: Boolean = false): List<RadioBeacon> {
      if (refresh) {
         PublicationVolume.values().forEach { volume ->
            val beacons = remoteDataSource.fetchRadioBeacons(volume)
            localDataSource.insert(beacons)
         }
      }

      return localDataSource.getRadioBeacons()
   }

   fun fetchRadioBeacons() {
      val fetchRequest = OneTimeWorkRequest.Builder(RefreshRadioBeaconWorker::class.java)
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
            FETCH_LATEST_RADIO_BEACONS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchRadioBeaconsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshRadioBeaconWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_RADIO_BEACONS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_RADIO_BEACONS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_RADIO_BEACONS_TASK = "FetchLatestRadioBeaconsTask"
      private const val TAG_FETCH_LATEST_RADIO_BEACONS = "FetchLatestRadioBeaconsTaskTag"
   }
}