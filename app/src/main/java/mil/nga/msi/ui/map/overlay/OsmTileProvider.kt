package mil.nga.msi.ui.map.overlay

import android.util.Log
import com.google.android.gms.maps.model.UrlTileProvider
import java.net.MalformedURLException
import java.net.URL

class OsmTileProvider(
   width: Int = 256,
   height: Int = 256,
) : UrlTileProvider(width, height) {

   override fun getTileUrl(x: Int, y: Int, z: Int): URL? {
      val url = "$baseUrl/${z}/${x}/${y}.png"
      return try {
         URL(url)
      } catch (e: MalformedURLException) {
         Log.w(LOG_NAME, "Problem with URL $url", e)
         null
      }
   }

   companion object {
      private val LOG_NAME = OsmTileProvider::class.java.name
      private const val baseUrl = "https://osm.gs.mil/tiles/default"
   }
}