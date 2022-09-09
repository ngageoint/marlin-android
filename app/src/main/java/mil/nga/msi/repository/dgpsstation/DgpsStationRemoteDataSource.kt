package mil.nga.msi.repository.dgpsstation

import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.dgpsstations.DgpsStationService
import java.util.*
import javax.inject.Inject

class DgpsStationRemoteDataSource @Inject constructor(
   private val service: DgpsStationService,
   private val localDataSource: DgpsStationLocalDataSource
) {
   suspend fun fetchDgpsStations(publicationVolume: PublicationVolume): List<DgpsStation> {
      val dgpsStations = mutableListOf<DgpsStation>()

      val latestDgpsStation = localDataSource.getLatestDgpsStation(publicationVolume.volumeTitle)
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestDgpsStation != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         val minYear = latestDgpsStation.noticeYear
         val minWeek = latestDgpsStation.noticeWeek.toInt()

         minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
         maxNoticeNumber = "${year}${"%02d".format(week + 1)}"
      }

      val filteredResponse = service.getDgpsStations(
         volume = publicationVolume.volumeQuery,
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (filteredResponse.isSuccessful) {
         val body = filteredResponse.body()
         if (latestDgpsStation != null && body?.isNotEmpty() == true) {
            // Pull all radio beacons again and save to ensure regions are set correctly
            val response = service.getDgpsStations(volume = publicationVolume.volumeQuery)
            if (response.isSuccessful) {
               // TODO do we need to remove lights that don't come back that we have locally?
               response.body()?.let { dgpsStations.addAll(it) }
            }
         } else {
            body?.let { dgpsStations.addAll(it) }
         }
      }

      return dgpsStations
   }
}