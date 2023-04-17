package mil.nga.msi.repository.layer

import android.net.Uri
import android.util.Log
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.network.layer.wms.WMSCapabilities
import mil.nga.msi.repository.preferences.Credentials
import javax.inject.Inject

class LayerRemoteDataSource @Inject constructor(
   private val service: LayerService
) {
   suspend fun getTile(url: String, credentials: Credentials? = null): Boolean {
      return url.toUri()?.let { uri ->
         val tileUri = uri.buildUpon()
            .appendPath("1")
            .appendPath("1")
            .appendPath("1.png")
            .build()

         val credentialsHeader = credentials?.let {
            okhttp3.Credentials.basic(it.username, it.password)
         }

         try {
            val response = service.getTile(
               url = tileUri.toString(),
               credentials = credentialsHeader
            )
            response.isSuccessful && response.body()?.contentType()?.type == "image"
         } catch (e: Exception) { false }
      } ?: false
   }

   suspend fun getWMSCapabilities(url: String, credentials: Credentials? = null): WMSCapabilities? {
      return url.toUri()?.let { uri: Uri ->
         val wmsUri = uri.buildUpon()
            .clearQuery()
            .appendQueryParameter("service", "WMS")
            .appendQueryParameter("version", "1.3.0")
            .appendQueryParameter("request", "GetCapabilities")
            .build()

         val credentialsHeader = credentials?.let {
            okhttp3.Credentials.basic(it.username, it.password)
         }

         try {
            val response = service.getWMSCapabilities(wmsUri.toString(), credentialsHeader)
            if (response.isSuccessful) {
               response.body()
            } else null
         } catch (e: Exception) { null }
      }
   }

   private fun String.toUri(): Uri? {
      return try {
         Uri.parse(this)
      } catch(e: Exception) { null }
   }
}