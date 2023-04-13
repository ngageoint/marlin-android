package mil.nga.msi.network.port

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PortService {
   @GET("/api/publications/world-port-index")
   suspend fun getPorts(
      @Query("output") output: String = "json"
   ): Response<PortResponse>
}