package mil.nga.msi.repository.layer

import android.net.Uri
import android.util.Log
import mil.nga.msi.network.layer.LayerService
import javax.inject.Inject

class LayerRemoteDataSource @Inject constructor(
   private val service: LayerService,
) {
   suspend fun getTile(url: String): Boolean {
      return url.toUri()?.let { uri ->
         val tileUri = uri.buildUpon()
            .appendPath("1")
            .appendPath("1")
            .appendPath("1.png")
            .build()

         try {
            val response = service.getTile(tileUri.toString())
            response.isSuccessful
         } catch (e: Exception) { false }
      } ?: false
   }

   private fun String.toUri(): Uri? {
      return try {
         Uri.parse(this)
      } catch(e: Exception) { null }
   }
}