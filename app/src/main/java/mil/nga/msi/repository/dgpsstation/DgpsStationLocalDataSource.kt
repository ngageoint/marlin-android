package mil.nga.msi.repository.dgpsstation

import androidx.sqlite.db.SimpleSQLiteQuery
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import javax.inject.Inject

class DgpsStationLocalDataSource @Inject constructor(
   private val dao: DgpsStationDao
) {
   fun observeDgpsStationListItems(query: SimpleSQLiteQuery) = dao.observeDgpsStationListItems(query)
   fun observeDgpsStationMapItems(query: SimpleSQLiteQuery) = dao.observeDgpsStationMapItems(query)

   fun isEmpty() = dao.count() == 0
   suspend fun existingDgpsStations(ids: List<String>) = dao.getDgpsStations(ids)

   fun observeDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ) = dao.observeDgpsStation(volumeNumber, featureNumber)

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ) = dao.getDgpsStation(volumeNumber, featureNumber)

   fun getDgpsStations(query: SimpleSQLiteQuery) = dao.getDgpsStations(query)

   suspend fun getDgpsStations(): List<DgpsStation> = dao.getDgpsStations()
   suspend fun getLatestDgpsStation(volumeNumber: String) = dao.getLatestDgpsStation(volumeNumber)

   suspend fun insert(dgpsStations: List<DgpsStation>) = dao.insert(dgpsStations)
}