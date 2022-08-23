package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.DataSource
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.pow

class PortTileProvider @Inject constructor(
   val application: Application,
   val repository: PortRepository
) : TileProvider {

   override fun getTile(x: Int, y: Int, z: Int): Tile? {
      val minTileLon = longitude(x = x, zoom = z)
      val maxTileLon = longitude(x = x + 1, zoom = z)
      val minTileLat = latitude(y = y + 1, zoom = z)
      val maxTileLat = latitude(y = y, zoom = z)

      val minQueryLon = longitude(x = x - 1, zoom = z)
      val maxQueryLon = longitude(x = x + 2, zoom = z)
      val minQueryLat = latitude(y = y + 2, zoom = z)
      val maxQueryLat = latitude(y = y - 1, zoom = z)

      val ports = repository.getPorts(
         minLatitude = minQueryLat,
         maxLatitude = maxQueryLat,
         minLongitude = minQueryLon,
         maxLongitude = maxQueryLon
      )

      if (ports.isEmpty()) return null

      val width = (application.resources.displayMetrics.density * 512).toInt()
      val height = (application.resources.displayMetrics.density * 512).toInt()

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      ports.forEach { port ->
         portImage()?.let { image ->
            val xPosition = (((port.longitude - minTileLon) / (maxTileLon - minTileLon)) * width)
            val yPosition = height - (((port.latitude - minTileLat) / (maxTileLat - minTileLat)) * height)
            val destination = Rect(
               xPosition.toInt() - image.width,
               yPosition.toInt() - image.height,
               xPosition.toInt() + image.width,
               yPosition.toInt() + image.height
            )

            canvas.drawBitmap(image, null, destination, null)
         }
      }

      val output = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
      return Tile(width, height, output.toByteArray())
   }

   private fun longitude(x: Int, zoom: Int): Double {
      return x.toDouble() / 2.0.pow(zoom.toDouble()) * 360.0 - 180.0
   }

   private fun latitude(y: Int, zoom: Int): Double {
      val n = PI - 2.0 * PI * y / 2.0.pow(zoom.toDouble())
      return 180.0 / PI * atan(0.5 * (exp(n) - exp(-n)))
   }

   private fun portImage(): Bitmap? {
      val size = (application.resources.displayMetrics.density * 14).toInt()
      val stroke = (application.resources.displayMetrics.density * 2)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = DataSource.PORT.color.toArgb()
            style = Paint.Style.FILL
            strokeWidth = stroke
         }
      )

      return bitmap
   }
}