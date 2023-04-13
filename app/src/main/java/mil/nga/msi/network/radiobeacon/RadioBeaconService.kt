package mil.nga.msi.network.radiobeacon

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBeaconService {
   @GET("/api/publications/ngalol/radiobeacons")
   suspend fun getRadioBeacons(
      @Query("volume") volume: String,
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
      @Query("includeRemovals") includeRemovals: String = "false"
   ): Response<RadioBeaconResponse>
}