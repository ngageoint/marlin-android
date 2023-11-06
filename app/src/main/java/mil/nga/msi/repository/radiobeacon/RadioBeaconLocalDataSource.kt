package mil.nga.msi.repository.radiobeacon

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao
import javax.inject.Inject

class RadioBeaconLocalDataSource @Inject constructor(
   private val dao: RadioBeaconDao
) {
   fun observeRadioBeaconMapItems(query: SimpleSQLiteQuery) = dao.observeRadioBeaconMapItems(query)
   fun observeRadioBeaconListItems(query: SimpleSQLiteQuery) = dao.observeRadioBeaconListItems(query)

   fun isEmpty() = dao.count() == 0
   suspend fun count(query: SimpleSQLiteQuery) = dao.count(query)

   suspend fun existingRadioBeacons(ids: List<String>) = dao.getRadioBeacons(ids)

   fun observeRadioBeacon(key: RadioBeaconKey): Flow<RadioBeacon> {
      return dao.observeRadioBeacon(key.volumeNumber, key.featureNumber)
   }

   suspend fun getRadioBeacon(key: RadioBeaconKey): RadioBeacon? {
      return dao.getRadioBeacon(key.volumeNumber, key.featureNumber)
   }

   fun getRadioBeacons(query: SimpleSQLiteQuery) = dao.getRadioBeacons(query)

   suspend fun getRadioBeacons(): List<RadioBeacon> = dao.getRadioBeacons()
   suspend fun getLatestRadioBeacon(volumeNumber: String) = dao.getLatestRadioBeacon(volumeNumber)

   suspend fun insert(beacons: List<RadioBeacon>) = dao.insert(beacons)
}