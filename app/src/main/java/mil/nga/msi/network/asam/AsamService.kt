package mil.nga.msi.network.asam

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AsamService {
   @GET("/api/publications/asam")
   suspend fun getAsams(
      @Query("sort") sort: String = "date",
      @Query("output") output: String = "json"
   ): Response<AsamResponse>
}