package mil.nga.msi.repository.light

import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightVolume
import mil.nga.msi.network.light.LightService
import java.util.*
import javax.inject.Inject

class LightRemoteDataSource @Inject constructor(
   private val service: LightService,
   private val localDataSource: LightLocalDataSource
) {
   suspend fun fetchLights(lightVolume: LightVolume): List<Light> {
      val lights = mutableListOf<Light>()

      val latestLight = localDataSource.getLatestLight(lightVolume.volumeTitle)
      var minNoticeNumber: String? = ""
      var maxNoticeNumber: String? = ""
      if (latestLight != null) {
         val calendar = Calendar.getInstance()
         val year = calendar.get(Calendar.YEAR)
         val week = calendar.get(Calendar.WEEK_OF_YEAR)

         minNoticeNumber = "${latestLight.noticeYear}${latestLight.noticeWeek}"
         maxNoticeNumber = "${year}${week + 1}"
      }

      val filteredResponse = service.getLights(
         volume = lightVolume.volumeQuery,
         minNoticeNumber = minNoticeNumber,
         maxNoticeNumber = maxNoticeNumber
      )

      if (filteredResponse.isSuccessful) {
         val body = filteredResponse.body()
         if (latestLight != null && body?.isNotEmpty() == true) {
            // Pull all lights again and save to ensure regions are set correctly
            val response = service.getLights(volume = lightVolume.volumeQuery)
            if (response.isSuccessful) {
               // TODO do we need to remove lights that don't come back that we have locally?
               response.body()?.let { lights.addAll(it) }
            }
         } else {
            body?.let { lights.addAll(it) }
         }
      }

      return lights
   }
}