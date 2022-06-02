package mil.nga.msi.repository.modu

import mil.nga.msi.datasource.modu.Modu
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ModuService {
   @GET("/api/publications/modu")
   suspend fun getModus(
      @Query("sort") sort: String = "date",
      @Query("output") output: String = "json",
      @Query("minOccurDate") minDate: String? = null,
      @Query("maxOccurDate") maxDate: String? = null,
   ): Response<List<Modu>>
}