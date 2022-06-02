package mil.nga.msi.network.asam

import mil.nga.msi.datasource.asam.Asam
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AsamService {
   @GET("/api/publications/asam")
   suspend fun getAsams(
      @Query("sort") sort: String = "date",
      @Query("output") output: String = "json",
      @Query("minOccurDate") minDate: String? = null,
      @Query("maxOccurDate") maxDate: String? = null,
   ): Response<List<Asam>>
}