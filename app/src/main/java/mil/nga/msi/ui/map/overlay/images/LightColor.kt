package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.ui.location.toDegrees
import mil.nga.sf.Point
import java.lang.Math.PI
import java.lang.Math.toRadians
import kotlin.math.*

private const val METERS_IN_NAUTICAL_MILE = 1852

fun colorImage(
   context: Context,
   colors: List<Color>,
   small: Boolean
): Bitmap {
   return if (small) {
      colorImageSmall(context, colors)
   } else {
      colorImageLarge(context, colors)
   }
}

fun colorRangeImage(
   context: Context,
   light: Light,
   colors: List<Color>,
   zoomLevel: Int,
   tileBounds: Bounds,
   tileSize: Double
): Bitmap? {
   val bitmap = Bitmap.createBitmap(tileSize.toInt(), tileSize.toInt(), Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)

   val latLng = LatLng(light.latitude, light.longitude)
   val lightColor = colors.first()
   val nauticalMiles = light.range?.toDoubleOrNull() ?: return null
   val nauticalMilesMeasurement = nauticalMiles * METERS_IN_NAUTICAL_MILE

   val coordinates = coordinates(latLng, nauticalMilesMeasurement)

   val path = Path()
   val initialPixel = toPixel(coordinates.first(), tileBounds, tileSize)
   path.moveTo(initialPixel.x.toFloat(), initialPixel.y.toFloat())
   coordinates.forEach { coordinate ->
      val pixel = toPixel(coordinate, tileBounds, tileSize)
      path.lineTo(pixel.x.toFloat(), pixel.y.toFloat())
   }
   path.close()

   canvas.drawPath(path, Paint().apply {
      strokeWidth = 12f
      color = lightColor.toArgb()
      style = Paint.Style.STROKE
   })

   canvas.drawPath(path, Paint().apply {
      strokeWidth = 12f
      color = lightColor.copy(alpha = .1f).toArgb()
      style = Paint.Style.FILL
   })

   // dot in the middle
   val middlePixel = toPixel(latLng, tileBounds, tileSize)
   val radius = zoomLevel / 3.0 * context.resources.displayMetrics.density * 1.5
   canvas.drawCircle(
      middlePixel.x.toFloat(),
      middlePixel.y.toFloat(),
      radius.toFloat(),
      Paint().apply {
         color = lightColor.toArgb()
         style = Paint.Style.FILL
      }
   )

   return bitmap
}

private fun colorImageSmall(
   context: Context,
   colors: List<Color>
): Bitmap {
   val size = (context.resources.displayMetrics.density * 8).toInt()

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

private fun colorImageLarge(
   context: Context,
   colors: List<Color>
): Bitmap {
   val size = (context.resources.displayMetrics.density * 48).toInt()
   val stroke = (context.resources.displayMetrics.density * 4).toInt()

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

private fun coordinates(center: LatLng, radiusInMeters: Double): List<LatLng> {
   val coordinates = mutableListOf<LatLng>()
   val centerLatitudeRadians = toRadians(center.latitude)
   val centerLongitudeRadians = toRadians(center.longitude)
   val dRadians = radiusInMeters / 6378137

   val radial = toRadians(0.0)
   val latitudeRadians = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(radial))
   val dLongitudeRadians = atan2(sin(radial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(dRadians))
   val longitudeRadians = ((centerLongitudeRadians + dLongitudeRadians + PI) % (2.0 * PI)) - PI
   coordinates.add(LatLng(latitudeRadians.toDegrees(), longitudeRadians.toDegrees()))

   for (i in 0..360) {
      val radial = toRadians(i.toDouble())
      val latitudeRadians = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(radial))
      val dLongitudeRadians = atan2(sin(radial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(latitudeRadians))
      val longitudeRadians = ((centerLongitudeRadians + dLongitudeRadians + PI) % (2.0 * PI)) - PI
      coordinates.add(LatLng(latitudeRadians.toDegrees(), longitudeRadians.toDegrees()))
   }

   val endRadial = toRadians(360.0)
   val endLatRad = asin(sin(centerLatitudeRadians) * cos(dRadians) + cos(centerLatitudeRadians) * sin(dRadians) * cos(endRadial))
   val endDlonRad = atan2(sin(endRadial) * sin(dRadians) * cos(centerLatitudeRadians), cos(dRadians) - sin(centerLatitudeRadians) * sin(endLatRad))
   val endLonRad = ((centerLongitudeRadians + endDlonRad + PI) % (2.0 * PI)) - PI
   coordinates.add(LatLng(endLatRad.toDegrees(), endLonRad.toDegrees()))

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
   val lambda = latLng.longitude / 180 * PI
   val phi = latLng.latitude / 180 * PI
   val x = a * lambda
   val y = a * ln(tan(PI / 4 + phi / 2))

   return Point(x, y)
}