package mil.nga.msi.network.layer

import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.network.Xml
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface LayerService {
   @GET
   suspend fun getTile(@Url url: String): Response<ResponseBody>

   @GET
   @Xml
   suspend fun getWMSCapabilities(@Url url: String): Response<WMSCapabilities>
}