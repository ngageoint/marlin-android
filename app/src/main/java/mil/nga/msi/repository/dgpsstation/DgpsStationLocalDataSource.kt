package mil.nga.msi.repository.dgpsstation

import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import javax.inject.Inject

class DgpsStationLocalDataSource @Inject constructor(
   private val dao: DgpsStationDao
) {
   fun observeDgpsStationListItems() = dao.getDgpsListItems()
   fun observeDgpsStationMapItems() = dao.getDgpsMapItems()

   fun observeDgpsStation(
      volumeNumber: String,
      featureNumber: Int
   ) = dao.observeDgpsStation(volumeNumber, featureNumber)

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Int
   ) = dao.getDgpsStation(volumeNumber, featureNumber)

   fun getDgpsStations(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = dao.getDgpsStations(minLatitude, maxLatitude, minLongitude, maxLongitude)

   suspend fun getDgpsStations(): List<DgpsStation> = dao.getDgpsStations()
   suspend fun getLatestDgpsStation(volumeNumber: String) = dao.getLatestDgpsStation(volumeNumber)

   suspend fun insert(dgpsStations: List<DgpsStation>) = dao.insert(dgpsStations)
}