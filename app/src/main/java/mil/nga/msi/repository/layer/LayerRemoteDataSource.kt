package mil.nga.msi.repository.layer

import android.net.Uri
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
         try {
            val extraParameters = mutableListOf<Pair<String, String>>()
            uri.queryParameterNames.forEach { parameter ->
               if (!GET_CAPABILITIES_PARAMETERS.contains(parameter)) {
                  uri.getQueryParameter(parameter)?.let { value ->
                     extraParameters.add(Pair(parameter, value))
                  }
               }
            }

            val builder = uri.buildUpon()
               .clearQuery()
               .appendQueryParameter("service", "WMS")
               .appendQueryParameter("version", "1.3.0")
               .appendQueryParameter("request", "GetCapabilities")

            extraParameters.forEach { builder.appendQueryParameter(it.first, it.second) }

            val credentialsHeader = credentials?.let {
               okhttp3.Credentials.basic(it.username, it.password)
            }

            val response = service.getWMSCapabilities(builder.build().toString(), credentialsHeader)
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

   companion object {
      val GET_CAPABILITIES_PARAMETERS = listOf("service", "version", "request")
   }
}