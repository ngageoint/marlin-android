package mil.nga.msi.ui.map.overlay

import android.net.Uri
import android.util.Log
import com.google.android.gms.maps.model.UrlTileProvider
import mil.nga.geopackage.tiles.TileBoundingBoxUtils
import java.net.MalformedURLException
import java.net.URL

open class GridTileProvider(
   private val baseUrl: Uri,
   width: Int = 256,
   height: Int = 256,
   private val invertYAxis: Boolean = false
) : UrlTileProvider(width, height) {

   override fun getTileUrl(x: Int, y: Int, z: Int): URL? {
      val yAxis = if (invertYAxis) {
         TileBoundingBoxUtils.getYAsOppositeTileFormat(z.toLong(), y)
      } else y

      val url = baseUrl.buildUpon()
         .appendPath("$z")
         .appendPath("$x")
         .appendPath("${yAxis}.png")
         .build()

      return try {
         URL(url.toString())
      } catch (e: MalformedURLException) {
         null
      }
   }
}