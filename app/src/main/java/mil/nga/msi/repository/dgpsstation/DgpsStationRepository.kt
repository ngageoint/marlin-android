package mil.nga.msi.repository.dgpsstation

import androidx.work.*
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.work.dgpsstation.RefreshDgpsStationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DgpsStationRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: DgpsStationLocalDataSource,
   private val remoteDataSource: DgpsStationRemoteDataSource
) {
   fun getDgpsStationListItems() = localDataSource.observeDgpsStationListItems()

   fun observeDgpsStation(
      volumeNumber: String,
      featureNumber: Int,
   ) = localDataSource.observeDgpsStation(volumeNumber, featureNumber)

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Int
   ) = localDataSource.getDgpsStation(volumeNumber, featureNumber)

   fun getDgpsStations(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getDgpsStations(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun fetchDgpsStations(refresh: Boolean = false): List<DgpsStation> {
      if (refresh) {
         PublicationVolume.values().forEach { volume ->
            val dgpsStations = remoteDataSource.fetchDgpsStations(volume)
            localDataSource.insert(dgpsStations)
         }
      }

      return localDataSource.getDgpsStations()
   }

   fun fetchDgpsStations() {
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
         .enqueueUniqueWork(
            FETCH_LATEST_DGPS_STATIONS_TASK,
            ExistingWorkPolicy.KEEP, fetchRequest
         )
   }

   fun fetchDgpsStationsPeriodically() {
      val fetchRequest = PeriodicWorkRequestBuilder<RefreshDgpsStationWorker>(
         REFRESH_RATE_HOURS, TimeUnit.HOURS
      ).setConstraints(
         Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
      ).addTag(TAG_FETCH_LATEST_DGPS_STATIONS)

      workManager.enqueueUniquePeriodicWork(
         FETCH_LATEST_DGPS_STATIONS_TASK,
         ExistingPeriodicWorkPolicy.KEEP,
         fetchRequest.build()
      )
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_DGPS_STATIONS_TASK = "FetchLatestDgpsStationsTask"
      private const val TAG_FETCH_LATEST_DGPS_STATIONS = "FetchLatestDgpsStationsTaskTag"
   }
}