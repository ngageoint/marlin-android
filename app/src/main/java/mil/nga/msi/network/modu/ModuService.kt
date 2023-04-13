package mil.nga.msi.network.modu

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ModuService {
   @GET("/api/publications/modu")
   suspend fun getModus(
      @Query("sort") sort: String = "date",
      @Query("output") output: String = "json",
      @Query("minSourceDate") minDate: String? = null,
      @Query("maxSourceDate") maxDate: String? = null,
   ): Response<ModuResponse>
}