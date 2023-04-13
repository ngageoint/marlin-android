package mil.nga.msi.repository.radiobeacon

import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.network.radiobeacon.RadioBeaconService
import java.util.*
import javax.inject.Inject

class RadioBeaconRemoteDataSource @Inject constructor(
   private val service: RadioBeaconService,
   private val localDataSource: RadioBeaconLocalDataSource
) {
   suspend fun fetchRadioBeacons(publicationVolume: PublicationVolume): List<RadioBeacon> {
      val beacons = mutableListOf<RadioBeacon>()

      val latestBeacon = localDataSource.getLatestRadioBeacon(publicationVolume.volumeTitle)
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestBeacon != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         val minYear = latestBeacon.noticeYear
         val minWeek = latestBeacon.noticeWeek.toInt()

         minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
         maxNoticeNumber = "${year}${"%02d".format(week + 1)}"
      }

      val filteredResponse = service.getRadioBeacons(
         volume = publicationVolume.volumeQuery,
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (filteredResponse.isSuccessful) {
         val body = filteredResponse.body()?.radioBeacons
         if (latestBeacon != null && body?.isNotEmpty() == true) {
            // Pull all radio beacons again and save to ensure regions are set correctly
            val response = service.getRadioBeacons(volume = publicationVolume.volumeQuery)
            if (response.isSuccessful) {
               // TODO do we need to remove lights that don't come back that we have locally?
               response.body()?.let { beacons.addAll(it.radioBeacons) }
            }
         } else {
            body?.let { beacons.addAll(it) }
         }
      }

      return beacons
   }
}