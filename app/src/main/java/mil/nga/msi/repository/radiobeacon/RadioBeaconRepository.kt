package mil.nga.msi.repository.radiobeacon

import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconMapItem
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.startup.radiobeacon.RadioBeaconInitializer.Companion.FETCH_LATEST_RADIO_BEACONS_TASK
import javax.inject.Inject

class RadioBeaconRepository @Inject constructor(
   workManager: WorkManager,
   private val localDataSource: RadioBeaconLocalDataSource,
   private val remoteDataSource: RadioBeaconRemoteDataSource,
   private val notification: MarlinNotification,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
) {
   @OptIn(ExperimentalCoroutinesApi::class)
   fun observeRadioBeaconMapItems(): Flow<List<RadioBeaconMapItem>> {
      return filterRepository.filters.flatMapLatest { entry ->
         val filters = entry[DataSource.RADIO_BEACON] ?: emptyList()
         val query = QueryBuilder("radio_beacons", filters).buildQuery()
         localDataSource.observeRadioBeaconMapItems(query)
      }
   }

   fun observeRadioBeaconListItems(filters: List<Filter>, sort: List<SortParameter>): PagingSource<Int, RadioBeacon> {
      val query = QueryBuilder(
         table = "radio_beacons",
         filters = filters,
         sort = sort
      ).buildQuery()

      return localDataSource.observeRadioBeaconListItems(query)
   }

   fun observeRadioBeacon(key: RadioBeaconKey) = localDataSource.observeRadioBeacon(key)
   suspend fun getRadioBeacon(key: RadioBeaconKey) = localDataSource.getRadioBeacon(key)

   fun getRadioBeacons(filters: List<Filter>): List<RadioBeacon> {
      val query = QueryBuilder("radio_beacons", filters).buildQuery()
      return localDataSource.getRadioBeacons(query)
   }

   suspend fun count(filters: List<Filter>): Int {
      val query = QueryBuilder(
         table = "radio_beacons",
         filters = filters,
      ).buildQuery(count = true)
      return localDataSource.count(query)
   }

   suspend fun fetchRadioBeacons(refresh: Boolean = false): List<RadioBeacon> {
      if (refresh) {
         val newBeacons = mutableListOf<RadioBeacon>()
         val fetched = userPreferencesRepository.fetched(DataSource.RADIO_BEACON)

         PublicationVolume.values().forEach { volume ->
            val beacons = remoteDataSource.fetchRadioBeacons(volume)

            if (fetched != null) {
               newBeacons.addAll(beacons.subtract(localDataSource.existingRadioBeacons(beacons.map { it.compositeKey() }).toSet()).toList())
            }

            localDataSource.insert(beacons)
         }

         notification.radioBeacon(newBeacons)
      }

      return localDataSource.getRadioBeacons()
   }

   val fetching = workManager.getWorkInfosForUniqueWorkLiveData(FETCH_LATEST_RADIO_BEACONS_TASK).map { workInfo ->
      workInfo.any { it.state == WorkInfo.State.RUNNING }
   }
}