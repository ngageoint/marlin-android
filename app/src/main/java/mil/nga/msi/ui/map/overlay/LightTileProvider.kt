package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.graphics.*
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightSector
import mil.nga.msi.repository.light.LightRepository
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
      if (z < 8) {
         Log.i("Billy", "Don't load Lights tile")
         return null
      }

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

      val width = 512
      val height = 512

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      lights.forEach { light ->
         mapImage(light, small = z < 13)?.let { image ->
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
   private fun mapImage(
      light: Light,
      small: Boolean
   ): Bitmap? {
      val sectors = light.lightSectors()
      return if (sectors.isNotEmpty()) {
         if (small) mapSectorsSmall(sectors) else mapSectorsLarge(sectors)
      } else null
   }

   private fun mapSectorsSmall(
      sectors: List<LightSector>
   ): Bitmap {
      val size = 32
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      sectors.forEach { sector ->
         val startAngle = sector.startDegrees + 90
         val endAngle = sector.endDegrees + 90
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else { endAngle.toFloat() }

         val paint = Paint().apply {
            color = sector.color.toArgb()
            style = Paint.Style.FILL
         }

         canvas.drawArc(
            RectF(0f, 0f, size.toFloat(), size.toFloat()),
            startAngle.toFloat(),
            sweepAngle,
            true,
            paint
         )
      }

      return bitmap
   }

   private fun mapSectorsLarge(
      sectors: List<LightSector>,
      includeLetters: Boolean = true,
      includeSectorDashes: Boolean = true
   ): Bitmap {
      val size = 256
      val stroke = 6f
      val center = PointF(size / 2f, size / 2f)
      val arcSize = size / 2

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

      sectors.forEach { sector ->
         val canvas = Canvas(bitmap)

         val startAngle = sector.startDegrees + 90
         val endAngle = sector.endDegrees + 90
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else {
            (360 - startAngle.toFloat()) + endAngle.toFloat()
         }

         canvas.drawArc(
            RectF(center.x / 2f, center.y / 2f, (center.x / 2f) + arcSize, (center.y / 2f) + arcSize),
            startAngle.toFloat(),
            sweepAngle,
            false,
            Paint().apply {
               isAntiAlias = true
               color = sector.color.toArgb()
               style = Paint.Style.STROKE
               strokeWidth = stroke
            }
         )

         if (includeSectorDashes) {
            val sectorDashLengthInPx = 256
            val paint = Paint().apply {
               color = Color.parseColor("#61000000")
               style = Paint.Style.STROKE
               strokeWidth = 2f
               pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
            }

            val path1 = Path()
            path1.moveTo(center.x, center.y)
            path1.lineTo(center.x + sectorDashLengthInPx, center.y)
            path1.transform(Matrix().apply { postRotate(sector.startDegrees.toFloat() + 90f, center.x , center.y) })
            canvas.drawPath(path1, paint)

            val path2 = Path()
            path2.moveTo(center.x, center.y)
            path2.lineTo(center.x + sectorDashLengthInPx, center.y)
            path2.transform(Matrix().apply { postRotate(sector.endDegrees.toFloat() + 90f, center.x , center.y) })
            canvas.drawPath(path2, paint)
         }

         if (includeLetters) {
            val textSize = 12f
            val midPointAngle = (sector.startDegrees) + (sector.endDegrees - sector.startDegrees) / 2.0

            val paint = Paint().apply {
               isAntiAlias = true
               setTextSize(textSize)
               color = Color.BLACK // TODO adjust for filled circle
               typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            canvas.translate(
               (size / 2f) - (paint.measureText(sector.text) / 2),
               size - (arcSize / 2f) - stroke
            )

            canvas.rotate(
               midPointAngle.toFloat(),
               (paint.measureText(sector.text) / 2),
               -(arcSize / 2f - stroke)
            )

            canvas.drawText(sector.text, 0f, 0f, paint)
         }
      }

      return bitmap
   }
}