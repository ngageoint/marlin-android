package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.graphics.*
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.ui.location.webMercatorToWgs84
import mil.nga.msi.ui.location.wgs84ToWebMercator
import mil.nga.sf.Point
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.*

class PortTileProvider @Inject constructor(
   val application: Application,
   val repository: PortRepository
) : TileProvider {

   override fun getTile(x: Int, y: Int, z: Int): Tile? {
      val width = (application.resources.displayMetrics.density * 512).toInt()
      val height = (application.resources.displayMetrics.density * 512).toInt()

      val minTileLon = longitude(x = x, zoom = z)
      val maxTileLon = longitude(x = x + 1, zoom = z)
      val minTileLat = latitude(y = y + 1, zoom = z)
      val maxTileLat = latitude(y = y, zoom = z)

      val neCorner3857 = Point(maxTileLon, maxTileLat).wgs84ToWebMercator()
      val swCorner3857 = Point(minTileLon, minTileLat).wgs84ToWebMercator()
      val minTileX = swCorner3857.x
      val minTileY = swCorner3857.y
      val maxTileX = neCorner3857.x
      val maxTileY = neCorner3857.y

      // Border tile by 20 miles, biggest light in MSI.
      // Border has to be at least 256 pixels as well
      val tolerance = max(20.0 * 1609.344, ((maxTileX - minTileX) / (width / 2)) * 12)

      val neCornerTolerance = Point(maxTileX + tolerance, maxTileY + tolerance).webMercatorToWgs84()
      val swCornerTolerance = Point(minTileX - tolerance, minTileY - tolerance).webMercatorToWgs84()
      val minQueryLon = swCornerTolerance.x
      val maxQueryLon = neCornerTolerance.x
      val minQueryLat = swCornerTolerance.y
      val maxQueryLat = neCornerTolerance.y

      val ports = repository.getPorts(
         minLatitude = minQueryLat,
         maxLatitude = maxQueryLat,
         minLongitude = minQueryLon,
         maxLongitude = maxQueryLon
      )

      if (ports.isEmpty()) return null

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      ports.forEach { port ->
         portImage()?.let { image ->
            val webMercator = Point(port.longitude, port.latitude).wgs84ToWebMercator()
            val xPosition = (((webMercator.x - minTileX) / (maxTileX - minTileX)) * width)
            val yPosition = height - (((webMercator.y - minTileY) / (maxTileY - minTileY)) * height)
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
      val size = (application.resources.displayMetrics.density * 12).toInt()
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