package mil.nga.msi.repository.light

import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.light.LightService
import java.util.*
import javax.inject.Inject

class LightRemoteDataSource @Inject constructor(
   private val service: LightService,
   private val localDataSource: LightLocalDataSource
) {
   suspend fun fetchLights(publicationVolume: PublicationVolume): List<Light> {
      val lights = mutableListOf<Light>()

      val latestLight = localDataSource.getLatestLight(publicationVolume.volumeTitle)
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestLight != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         val minYear = latestLight.noticeYear
         val minWeek = latestLight.noticeWeek.toInt()

         minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
         maxNoticeNumber = "${year}${"%02d".format(week + 1)}"
      }

      val filteredResponse = service.getLights(
         volume = publicationVolume.volumeQuery,
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (filteredResponse.isSuccessful) {
         val body = filteredResponse.body()?.lights
         if (latestLight != null && body?.isNotEmpty() == true) {
            // Pull all lights again and save to ensure regions are set correctly
            val response = service.getLights(volume = publicationVolume.volumeQuery)
            if (response.isSuccessful) {
               // TODO do we need to remove lights that don't come back that we have locally?
               response.body()?.let { lights.addAll(it.lights) }
            }
         } else {
            body?.let { lights.addAll(it) }
         }
      }

      return lights
   }
}