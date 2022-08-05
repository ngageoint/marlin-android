package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightColor
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
         mapImages(light, small = z < 13).forEach { image ->
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
   private fun mapImages(
      light: Light,
      small: Boolean
   ): List<Bitmap> {
      val images = mutableListOf<Bitmap>()

      if (light.isFogSignal()) {
         images.add(fogSignal())
      }

      if (light.isBuoy()) {
         images.add(buoy())
      }

      val sectors = light.lightSectors()
      val colors = light.lightColors()
      if (sectors.isNotEmpty()) {
         val image = if (small) mapSectorsSmall(sectors) else mapSectorsLarge(sectors)
         images.add(image)
      } else if(colors.isNotEmpty()) {
         val image = if (small) mapColorsSmall(colors) else mapColorsLarge(colors)
         images.add(image)
      } else {
         val image = if (small) mapRaconSmall() else mapRaconLarge()
         images.add(image)
      }

      return images
   }

   private fun buoy(): Bitmap {
      val size = (application.resources.displayMetrics.density * 16).toInt()
      val stroke = (application.resources.displayMetrics.density * 2)

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = LightColor.BUOY.color.toArgb()
            style = Paint.Style.FILL
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (application.resources.displayMetrics.density * 1f),
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.FILL
            strokeWidth = (application.resources.displayMetrics.density * 1)
         }
      )

      return bitmap
   }

   private fun fogSignal(): Bitmap {
      val size = (application.resources.displayMetrics.density * 120).toInt()
      val center = PointF(size / 2f, size / 2f)

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val arc1Size = (application.resources.displayMetrics.density * 24)
      canvas.drawArc(
         RectF((center.x) - (arc1Size / 2f),
            (center.y) - (arc1Size / 2f),
            (center.x) + (arc1Size / 2f),
            (center.y) + (arc1Size / 2f)),
         315f,
         45f,
         false,
         Paint().apply {
            color = LightColor.RACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 2)
         }
      )

      val arc2Size = (application.resources.displayMetrics.density * 36)
      canvas.drawArc(
         RectF((center.x) - (arc2Size / 2f),
            (center.y) - (arc2Size / 2f),
            (center.x) + (arc2Size / 2f),
            (center.y) + (arc2Size / 2f)),
         315f,
         45f,
         false,
         Paint().apply {
            color = LightColor.RACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 2)
         }
      )

      val arc3Size = (application.resources.displayMetrics.density * 48)
      canvas.drawArc(
         RectF((center.x) - (arc3Size / 2f),
            (center.y) - (arc3Size / 2f),
            (center.x) + (arc3Size / 2f),
            (center.y) + (arc3Size / 2f)),
         315f,
         45f,
         false,
         Paint().apply {
            color = LightColor.RACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 2)
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (application.resources.displayMetrics.density * 4),
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 2)
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (application.resources.displayMetrics.density * 1f),
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.FILL
            strokeWidth = (application.resources.displayMetrics.density * 1)
         }
      )

      return bitmap
   }

   private fun mapSectorsSmall(
      sectors: List<LightSector>
   ): Bitmap {
      val size = (application.resources.displayMetrics.density * 16).toInt()
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

   private fun mapColorsSmall(
      colors: List<Color>
   ): Bitmap {
      val size = (application.resources.displayMetrics.density * 8).toInt()

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val count = 0f
      val degreesPerColor = 360.0 / colors.size.toFloat()
      colors.forEach { color ->
         val startAngle = degreesPerColor * count
         val endAngle = degreesPerColor * (count + 1f)
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else { endAngle.toFloat() }

         val paint = Paint().apply {
            setColor(color.toArgb())
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

   private fun mapColorsLarge(
      colors: List<Color>
   ): Bitmap {
      val size = (application.resources.displayMetrics.density * 48).toInt()
      val stroke = (application.resources.displayMetrics.density * 4).toInt()

      val arcSize = size / 2
      val center = PointF(size / 2f, size / 2f)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      val count = 0f
      val degreesPerColor = 360.0 / colors.size.toFloat()
      colors.forEach { color ->
         val startAngle = degreesPerColor * count
         val endAngle = degreesPerColor * (count + 1f)
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else { endAngle.toFloat() }

         canvas.drawArc(
            RectF(center.x / 2f, center.y / 2f, (center.x / 2f) + arcSize, (center.y / 2f) + arcSize),
            startAngle.toFloat(),
            sweepAngle,
            false,
            Paint().apply {
               setColor(color.toArgb())
               isAntiAlias = true
               style = Paint.Style.STROKE
               strokeWidth = stroke.toFloat()
            }
         )

         val towerLine = Path()
         towerLine.moveTo(center.x, center.y)
         towerLine.lineTo(center.x,  center.y - (arcSize / 2))
         canvas.drawPath(towerLine, Paint().apply {
            setColor(color.toArgb())
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = stroke.toFloat()
         })
      }

      return bitmap
   }

   private fun mapSectorsLarge(
      sectors: List<LightSector>,
      includeLetters: Boolean = true,
      includeSectorDashes: Boolean = true
   ): Bitmap {
      val size = (application.resources.displayMetrics.density * 256).toInt()
      val stroke = (application.resources.displayMetrics.density * 6).toInt()
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
               strokeWidth = stroke.toFloat()
            }
         )

         if (includeSectorDashes) {
            val sectorDashLength = (application.resources.displayMetrics.density * 128).toInt()
            val sectorDashInterval = (application.resources.displayMetrics.density * 2)
            val paint = Paint().apply {
               color = Color(0x33000000).toArgb()
               style = Paint.Style.STROKE
               strokeWidth = (application.resources.displayMetrics.density * 1)
               pathEffect = DashPathEffect(floatArrayOf(sectorDashInterval, sectorDashInterval), 0f)
            }

            val path1 = Path()
            path1.moveTo(center.x, center.y)
            path1.lineTo(center.x + sectorDashLength, center.y)
            path1.transform(Matrix().apply { postRotate(sector.startDegrees.toFloat() + 90f, center.x , center.y) })
            canvas.drawPath(path1, paint)

            val path2 = Path()
            path2.moveTo(center.x, center.y)
            path2.lineTo(center.x + sectorDashLength, center.y)
            path2.transform(Matrix().apply { postRotate(sector.endDegrees.toFloat() + 90f, center.x , center.y) })
            canvas.drawPath(path2, paint)
         }

         if (includeLetters) {
            val midPointAngle = (sector.startDegrees) + (sector.endDegrees - sector.startDegrees) / 2.0

            val paint = Paint().apply {
               isAntiAlias = true
               textSize = (application.resources.displayMetrics.density * 12)
               color = Color.Black.toArgb() // TODO adjust for filled circle
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

   private fun mapRaconSmall(): Bitmap {
      val size = (application.resources.displayMetrics.density * 20).toInt()
      val stroke = (application.resources.displayMetrics.density * 2)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = LightColor.RACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
         }
      )

      return bitmap
   }

   private fun mapRaconLarge(): Bitmap {
      val size = (application.resources.displayMetrics.density * 120).toInt()

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         size / 4f,
         Paint().apply {
            color = LightColor.RACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 4)
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (application.resources.displayMetrics.density * 4),
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = (application.resources.displayMetrics.density * 2)
         }
      )

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (application.resources.displayMetrics.density * 1f),
         Paint().apply {
            color = Color.Black.toArgb()
            style = Paint.Style.FILL
            strokeWidth = (application.resources.displayMetrics.density * 1)
         }
      )

      return bitmap
   }
}