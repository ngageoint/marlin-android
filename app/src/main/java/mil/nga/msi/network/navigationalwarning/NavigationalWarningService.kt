package mil.nga.msi.network.navigationalwarning

import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NavigationalWarningService {
   @GET("/api/publications/broadcast-warn")
   suspend fun getNavigationalWarnings(
      @Query("status") status: String = "active",
      @Query("output") output: String = "json"
   ): Response<List<NavigationalWarning>>
}