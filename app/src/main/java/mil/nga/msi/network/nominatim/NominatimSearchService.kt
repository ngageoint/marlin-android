package mil.nga.msi.network.nominatim

import mil.nga.msi.ui.map.search.NominatimResultItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NominatimSearchService{
   @Headers("User-Agent: marlin-android")
   @GET("search")
   suspend fun search(
      @Query("q") text: String,
      @Query("format") format: String = "json",
   ): Response<List<NominatimResultItem>>
}