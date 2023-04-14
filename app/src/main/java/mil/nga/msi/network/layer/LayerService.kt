package mil.nga.msi.network.layer

import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.network.Xml
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface LayerService {
   @GET
   suspend fun getTile(
      @Url url: String,
      @Header("Authorization") credentials: String? = null
   ): Response<ResponseBody>

   @GET
   @Xml
   suspend fun getWMSCapabilities(
      @Url url: String,
      @Header("Authorization") credentials: String? = null
   ): Response<WMSCapabilities>
}