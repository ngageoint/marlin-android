package mil.nga.msi.ui.map.overlay

import android.net.Uri
import mil.nga.msi.network.layer.LayerService
import mil.nga.msi.repository.preferences.Credentials
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

class WMSTileProvider(
   service: LayerService,
   credentials: Credentials? = null,
   width: Int = 256,
   height: Int = 256,
   private val url: String,
   private val minZoom: Int? = null,
   private val maxZoom: Int? = null
) : MarlinTileProvider(service, credentials, width, height) {

   override fun getTileUrl(x: Int, y: Int, z: Int): URL? {
      if (!withinZoom(z, minZoom, maxZoom)) return null

      val builder = Uri.parse(url).buildUpon()
         .appendQueryParameter("BBOX", bbox(x, y, z))

      val url = builder.build().toString()

      return try {
         URL(url)
      } catch (e: MalformedURLException) { null }
   }

   private fun mercatorXOfLongitude(lon: Double): Double {
      return lon * 20037508.34 / 180
   }

   private fun getX(x: Int, z: Int): Double {
      return x / 2.0.pow(z.toDouble()) * 360.0 - 180
   }

   private fun getY(y: Int, z: Int): Double {
      val n = Math.PI - 2.0 * Math.PI * y / 2.0.pow(z.toDouble())
      return 180.0 / Math.PI * atan(0.5 * (exp(n) - exp(-n)))
   }

   private fun mercatorYOfLatitude(lat: Double): Double {
      var y = ln(tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180)
      y = y * 20037508.34 / 180
      return y
   }

   private fun bbox(x: Int, y: Int, z: Int): String {
      val left = mercatorXOfLongitude(getX(x, z))
      val right = mercatorXOfLongitude(getX(x + 1, z))
      val bottom = mercatorYOfLatitude(getY(y + 1, z))
      val top = mercatorYOfLatitude(getY(y, z))
      return "$left,$bottom,$right,$top"
   }
}