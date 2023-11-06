package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightSector
import mil.nga.msi.ui.location.toDegrees
import mil.nga.sf.Point
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan

private const val METERS_IN_NAUTICAL_MILE = 1852

fun sectorImage(
   context: Context,
   sectors: List<LightSector>,
   small: Boolean
): Bitmap {
   return if (small) {
      sectorImageSmall(context, sectors)
   } else {
      sectorImageLarge(context, sectors)
   }
}

fun sectorRangeImage(
   light: Light,
   sectors: List<LightSector>,
   tileBounds: Bounds,
   tileSize: Double
): Bitmap {
   val bitmap = Bitmap.createBitmap(tileSize.toInt(), tileSize.toInt(), Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)

   val latLng = LatLng(light.latitude, light.longitude)

   sectors
      .asSequence()
      .sortedBy { it.range }
      .filterNot {
         // Error in the data, or sometimes lights are defined as follows:
         // characteristic Q.W.R.
         // remarks R. 289°-007°, W.-007°.
         // That would mean this light flashes between red and white over those angles.
         // TODO: figure out what to do with multi colored lights over the same sector
         it.startDegrees >= it.endDegrees
      }
      .filterNot { sector -> sector.obscured }
      .forEach { sector ->
         val nauticalMiles = sector.range ?: 0.0
         val nauticalMilesMeasurement = nauticalMiles * METERS_IN_NAUTICAL_MILE

         val coordinates = sectorCoordinates(
            center = latLng,
            range = nauticalMilesMeasurement,
            startDegrees = sector.startDegrees + 180.0,
            endDegrees = sector.endDegrees + 180.0
         )

         val path = Path()
         val centerPixel = toPixel(latLng, tileBounds, tileSize)
         path.moveTo(centerPixel.x.toFloat(), centerPixel.y.toFloat())
         coordinates.forEach { coordinate ->
            val pixel = toPixel(coordinate, tileBounds, tileSize)
            path.lineTo(pixel.x.toFloat(), pixel.y.toFloat())
         }
         path.close()

         canvas.drawPath(path, Paint().apply {
            strokeWidth = 6f
            color = sector.color.toArgb()
            style = Paint.Style.STROKE
         })

         canvas.drawPath(path, Paint().apply {
            color = sector.color.copy(alpha = .1f).toArgb()
            style = Paint.Style.FILL
         })
      }

   return bitmap
}

private fun sectorImageSmall(
   context: Context,
   sectors: List<LightSector>
): Bitmap {
   val size = (context.resources.displayMetrics.density * 8).toInt()
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

private fun sectorImageLarge(
   context: Context,
   sectors: List<LightSector>
): Bitmap {
   val size = (context.resources.displayMetrics.density * 128).toInt()
   val center = PointF(size / 2f, size / 2f)
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

      val arcSize = size / 2f
      val characteristicNumber = sector.characteristicNumber ?: 1
      val offset = ((8 * context.resources.displayMetrics.density) * (characteristicNumber - 1))
      val radius = (arcSize / 2f) - offset
      val oval = RectF(
         arcSize - radius,
         arcSize - radius,
         arcSize + radius,
         arcSize + radius
      )

      val path = Path()
      path.addArc(
         oval,
         startAngle.toFloat(),
         sweepAngle
      )

      val sectorPaint = if (sector.obscured) {
         val stroke = (context.resources.displayMetrics.density * 2)
         Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = stroke
            color = Color(0x61000000).toArgb()
            pathEffect = DashPathEffect(floatArrayOf(stroke, stroke), 0f)
         }
      } else {
         val stroke = (context.resources.displayMetrics.density * 3)
         Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = stroke
            color = sector.color.toArgb()
         }
      }

      canvas.drawArc(
         oval,
         startAngle.toFloat(),
         sweepAngle,
         false,
         sectorPaint
      )

      val sectorDashLength = (context.resources.displayMetrics.density * 64).toInt()
      val sectorDashInterval = (context.resources.displayMetrics.density * 1)
      val paint = Paint().apply {
         color = Color(0x33000000).toArgb()
         style = Paint.Style.STROKE
         strokeWidth = (context.resources.displayMetrics.density * .5f)
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

      sector.text?.let { text ->
         val stroke = (context.resources.displayMetrics.density * 3)
         val midPointAngle = (sector.startDegrees) + (sector.endDegrees - sector.startDegrees) / 2.0
         canvas.translate(
            (size / 2f) - (paint.measureText(sector.text) / 2),
            size - (arcSize / 2f) - offset - stroke
         )

         canvas.rotate(
            midPointAngle.toFloat(),
            (paint.measureText(sector.text) / 2),
            -((arcSize / 2f) - offset - stroke)
         )

         canvas.drawText(text, 0f, 0f, Paint().apply {
            isAntiAlias = true
            textSize = (context.resources.displayMetrics.density * 4)
            color = Color.Black.toArgb()
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
         })
      }
   }

   return bitmap
}

// TODO this lives in two places, refactor
fun sectorCoordinates(
   center: LatLng,
   range: Double,
   startDegrees: Double,
   endDegrees: Double
): List<LatLng> {
   val coordinates = mutableListOf<LatLng>()
   val centerLatitudeRadians = Math.toRadians(center.latitude)
   val centerLongitudeRadians = Math.toRadians(center.longitude)
   val dRadians = range / 6378137

   val startRadial = Math.toRadians(startDegrees)
   val startLatitudeRadians = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(startRadial))
   val startDLongitudeRadians = atan2(sin(startRadial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(startLatitudeRadians))
   val startLongitudeRadians = ((centerLongitudeRadians + startDLongitudeRadians + Math.PI) % (2.0 * Math.PI)) - Math.PI
   coordinates.add(LatLng(startLatitudeRadians.toDegrees(), startLongitudeRadians.toDegrees()))

   for (i in startDegrees.toInt()..endDegrees.toInt()) {
      val radial = Math.toRadians(i.toDouble())
      val latitudeRadians = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(radial))
      val dLongitudeRadians = atan2(sin(radial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(latitudeRadians))
      val longitudeRadians = ((centerLongitudeRadians + dLongitudeRadians + PI) % (2.0 * PI)) - PI
      coordinates.add(LatLng(latitudeRadians.toDegrees(), longitudeRadians.toDegrees()))
   }

   val endRadial = Math.toRadians(endDegrees)
   val endLatitudeRadians = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(endRadial))
   val endDLongitudeRadians = atan2(sin(endRadial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(endLatitudeRadians))
   val endLongitudeRadians = ((centerLongitudeRadians + endDLongitudeRadians + PI) % (2.0 * PI)) - PI
   coordinates.add(LatLng(endLatitudeRadians.toDegrees(), endLongitudeRadians.toDegrees()))

   return coordinates
}

private fun toPixel(latLng: LatLng, tileBounds3857: Bounds, tileSize: Double): Point {
   val object3857Location = to3857(latLng)
   val xPosition = (((object3857Location.x - tileBounds3857.minX) / (tileBounds3857.maxX - tileBounds3857.minX)) * tileSize)
   val yPosition = tileSize - (((object3857Location.y - tileBounds3857.minY) / (tileBounds3857.maxY - tileBounds3857.minY)) * tileSize)
   return Point(xPosition, yPosition)
}

private fun to3857(latLng: LatLng): Point {
   val a = 6378137.0
   val lambda = latLng.longitude / 180 * Math.PI
   val phi = latLng.latitude / 180 * Math.PI
   val x = a * lambda
   val y = a * ln(tan(Math.PI / 4 + phi / 2))

   return Point(x, y)
}