package mil.nga.msi.network.light

import mil.nga.msi.datasource.light.Light
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LightService {
   @GET("/api/publications/ngalol/lights-buoys")
   suspend fun getLights(
      @Query("volume") volume: String,
      @Query("output") output: String = "json",
      @Query("minNoticeNumber") minNoticeNumber: String? = null,
      @Query("maxNoticeNumber") maxNoticeNumber: String? = null,
      @Query("includeRemovals") includeRemovals: String = "false"
   ): Response<List<Light>>
}