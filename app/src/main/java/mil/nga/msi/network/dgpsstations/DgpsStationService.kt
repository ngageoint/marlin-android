package mil.nga.msi.network.dgpsstations

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DgpsStationService {
   @GET("/api/publications/ngalol/dgpsstations")
   suspend fun getDgpsStations(
      @Query("volume") volume: String,
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
      @Query("includeRemovals") includeRemovals: String = "false"
   ): Response<DgpsStationResponse>
}