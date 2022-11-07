package mil.nga.msi.repository.dgpsstation

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationListItem
import mil.nga.msi.datasource.dgpsstation.DgpsStationMapItem
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.work.dgpsstation.LoadDgpsStationWorker
import mil.nga.msi.work.dgpsstation.RefreshDgpsStationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DgpsStationRepository @Inject constructor(
   private val workManager: WorkManager,
   private val localDataSource: DgpsStationLocalDataSource,
   private val remoteDataSource: DgpsStationRemoteDataSource,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val filterRepository: FilterRepository,
   private val notification: MarlinNotification
) {

   @OptIn(ExperimentalCoroutinesApi::class)
   fun observeDgpsStationMapItems(): Flow<List<DgpsStationMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.DGPS_STATION] ?: emptyList()
         val query = QueryBuilder("dgps_stations", filters).buildQuery()
         localDataSource.observeDgpsStationMapItems(query)
      }
   }

   fun observeDgpsStationListItems(filters: List<Filter>): PagingSource<Int, DgpsStationListItem> {
      val query = QueryBuilder("dgps_stations", filters).buildQuery()
      return localDataSource.observeDgpsStationListItems(query)
   }

   fun observeDgpsStation(
      volumeNumber: String,
      featureNumber: Float,
   ) = localDataSource.observeDgpsStation(volumeNumber, featureNumber)

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ) = localDataSource.getDgpsStation(volumeNumber, featureNumber)

   fun getDgpsStations(filters: List<Filter>): List<DgpsStation> {
      val query = QueryBuilder("dgps_stations", filters).buildQuery()
      return localDataSource.getDgpsStations(query)
   }

   suspend fun fetchDgpsStations(refresh: Boolean = false): List<DgpsStation> {
      if (refresh) {
         val newStations = mutableListOf<DgpsStation>()

         val fetched = userPreferencesRepository.fetched(DataSource.DGPS_STATION)
         PublicationVolume.values().forEach { volume ->
            val stations = remoteDataSource.fetchDgpsStations(volume)

            if (fetched == null) {
               newStations.addAll(stations.subtract(localDataSource.existingDgpsStations(stations.map { it.compositeKey() }).toSet()).toList())
            }

            localDataSource.insert(stations)
         }

         notification.dgpsStation(newStations)
      }

      return localDataSource.getDgpsStations()
   }

   fun fetchDgpsStations() {
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

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_DGPS_STATIONS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }

   companion object {
      private const val REFRESH_RATE_HOURS = 24L
      private const val FETCH_LATEST_DGPS_STATIONS_TASK = "FetchLatestDgpsStationsTask"
      private const val TAG_FETCH_LATEST_DGPS_STATIONS = "FetchLatestDgpsStationsTaskTag"
   }
}