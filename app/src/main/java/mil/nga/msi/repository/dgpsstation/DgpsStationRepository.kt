package mil.nga.msi.repository.dgpsstation

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationMapItem
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.startup.dgpsstation.DgpsStationInitializer.Companion.FETCH_LATEST_DGPS_STATIONS_TASK
import javax.inject.Inject

class DgpsStationRepository @Inject constructor(
   workManager: WorkManager,
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

   fun observeDgpsStationListItems(filters: List<Filter>, sort: List<SortParameter>): PagingSource<Int, DgpsStation> {
      val query = QueryBuilder(
         table = "dgps_stations",
         filters = filters,
         sort = sort
      ).buildQuery()

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

   suspend fun count(filters: List<Filter>): Int {
      val query = QueryBuilder(
         table = "dgps_stations",
         filters = filters,
      ).buildQuery(count = true)
      return localDataSource.count(query)
   }

   suspend fun fetchDgpsStations(refresh: Boolean = false): List<DgpsStation> {
      if (refresh) {
         val newStations = mutableListOf<DgpsStation>()

         val fetched = userPreferencesRepository.fetched(DataSource.DGPS_STATION)
         PublicationVolume.entries.forEach { volume ->
            val stations = remoteDataSource.fetchDgpsStations(volume)

            if (fetched != null) {
               newStations.addAll(stations.subtract(localDataSource.existingDgpsStations(stations.map { it.compositeKey() }).toSet()).toList())
            }

            localDataSource.insert(stations)
         }

         notification.dgpsStation(newStations)
      }

      return localDataSource.getDgpsStations()
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_DGPS_STATIONS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}