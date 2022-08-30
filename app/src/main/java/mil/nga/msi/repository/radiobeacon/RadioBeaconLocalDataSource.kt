package mil.nga.msi.repository.radiobeacon

import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconDao
import javax.inject.Inject

class RadioBeaconLocalDataSource @Inject constructor(
   private val dao: RadioBeaconDao
) {
//   fun observeMapItems() = dao.observeMapItems()
   fun observeRadioBeaconListItems() = dao.getRadioBeaconListItems()

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

//   fun getLights(
//      minLatitude: Double,
//      maxLatitude: Double,
//      minLongitude: Double,
//      maxLongitude: Double,
//      characteristicNumber: Int
//   ) = dao.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber)

   suspend fun getRadioBeacons(): List<RadioBeacon> = dao.getRadioBeacons()
   suspend fun getLatestRadioBeacon(volumeNumber: String) = dao.getLatestRadioBeacon(volumeNumber)
//
   suspend fun insert(beacons: List<RadioBeacon>) = dao.insert(beacons)
}