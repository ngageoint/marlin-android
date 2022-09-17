package mil.nga.msi.repository.radiobeacon

import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao
import javax.inject.Inject

class RadioBeaconLocalDataSource @Inject constructor(
   private val dao: RadioBeaconDao
) {
   fun observeRadioBeaconMapItems() = dao.observeRadioBeaconMapItems()
   fun observeRadioBeaconListItems() = dao.observeRadioBeaconListItems()

   fun isEmpty() = dao.count() == 0
   suspend fun existingRadioBeacons(ids: List<String>) = dao.existingRadioBeacons(ids)

   fun observeRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ) = dao.observeRadioBeacon(volumeNumber, featureNumber)

   suspend fun getRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ) = dao.getRadioBeacon(volumeNumber, featureNumber)

   fun getRadioBeacons(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun getRadioBeacons(): List<RadioBeacon> = dao.getRadioBeacons()
   suspend fun getLatestRadioBeacon(volumeNumber: String) = dao.getLatestRadioBeacon(volumeNumber)

   suspend fun insert(beacons: List<RadioBeacon>) = dao.insert(beacons)
}