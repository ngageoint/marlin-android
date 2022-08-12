package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.graphics.*
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.ui.map.overlay.images.*
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.pow

class LightTileProvider @Inject constructor(
   val application: Application,
   val repository: LightRepository
) : TileProvider {

   override fun getTile(x: Int, y: Int, z: Int): Tile? {
      if (z < 8) return null

      val minTileLon = longitude(x = x, zoom = z)
      val maxTileLon = longitude(x = x + 1, zoom = z)
      val minTileLat = latitude(y = y + 1, zoom = z)
      val maxTileLat = latitude(y = y, zoom = z)

      val minQueryLon = longitude(x = x - 1, zoom = z)
      val maxQueryLon = longitude(x = x + 2, zoom = z)
      val minQueryLat = latitude(y = y + 2, zoom = z)
      val maxQueryLat = latitude(y = y - 1, zoom = z)

      val lights = repository.getLights(
         minLatitude = minQueryLat,
         maxLatitude = maxQueryLat,
         minLongitude = minQueryLon,
         maxLongitude = maxQueryLon
      )

      if (lights.isEmpty()) return null

      val width = (application.resources.displayMetrics.density * 512).toInt()
      val height = (application.resources.displayMetrics.density * 512).toInt()

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      lights.forEach { light ->
         lightImages(light, small = z < 13).forEach { image ->
            val xPosition = (((light.longitude - minTileLon) / (maxTileLon - minTileLon)) * width)
            val yPosition = height - (((light.latitude - minTileLat) / (maxTileLat - minTileLat)) * height)
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

   // TODO move to common class to draw on canvas so maybe compose code can use this
   private fun lightImages(
      light: Light,
      small: Boolean
   ): List<Bitmap> {
      val images = mutableListOf<Bitmap>()

      if (light.isFogSignal()) {
         images.add(fogSignal(application))
      }

      if (light.isBuoy()) {
         images.add(buoyImage(application))
      }

      val sectors = light.lightSectors()
      val colors = light.lightColors()
      if (sectors.isNotEmpty()) {
         images.add(sectorImage(application, sectors, small))
      } else if(colors.isNotEmpty()) {
         images.add(colorImage(application, colors, small))
      } else {
         images.add(light.raconImage(application, small))
      }

      return images
   }
}