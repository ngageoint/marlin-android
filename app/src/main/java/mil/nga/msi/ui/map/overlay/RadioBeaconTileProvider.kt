package mil.nga.msi.ui.map.overlay

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
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position

class RadioBeaconImage(
   private val beacon: RadioBeacon
): DataSourceImage {
   override val dataSource = DataSource.RADIO_BEACON
   override val feature: Feature =
      Feature(
         Point(
            Position(beacon.longitude, beacon.latitude)
         )
      )

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      val image = if (zoom < 13) {
         pointImage(context, zoom)
      } else {
         sectorImageLarge(context, beacon)
      }

      return listOf(image)
   }

   private fun sectorImageLarge(
      context: Context,
      beacon: RadioBeacon
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 128).toInt()
      val stroke = (context.resources.displayMetrics.density * 2).toInt()
      val center = PointF(size / 2f, size / 2f)
      val arcSize = size / 8

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      beacon.azimuthCoverage().forEach { sector ->
         canvas.drawCircle(
            size / 2f,
            size / 2f,
            (context.resources.displayMetrics.density * 2),
            Paint().apply {
               color = Color.Black.toArgb()
               style = Paint.Style.STROKE
               strokeWidth = (context.resources.displayMetrics.density * .25f)
            }
         )

         canvas.drawCircle(
            size / 2f,
            size / 2f,
            (context.resources.displayMetrics.density * 1f),
            Paint().apply {
               color = Color.Black.toArgb()
               style = Paint.Style.FILL
            }
         )

         val startAngle = sector.startDegrees
         val endAngle = sector.endDegrees
         val sweepAngle = if (startAngle < endAngle ) {
            endAngle.toFloat() - startAngle.toFloat()
         } else {
            (360 - startAngle.toFloat()) + endAngle.toFloat()
         }

         canvas.drawArc(
            RectF(center.x - arcSize, center.y - arcSize, center.x + arcSize, center.y + arcSize),
            startAngle.toFloat(),
            sweepAngle,
            true,
            Paint().apply {
               isAntiAlias = true
               color = sector.color.toArgb()
               style = Paint.Style.STROKE
               strokeWidth = stroke.toFloat()
            }
         )

         if (sweepAngle < 360) {
            val sectorDashLength = arcSize * 4
            val sectorDashInterval = (context.resources.displayMetrics.density * 2)
            val paint = Paint().apply {
               color = Color(0x33000000).toArgb()
               style = Paint.Style.STROKE
               strokeWidth = (context.resources.displayMetrics.density * 1)
               pathEffect = DashPathEffect(floatArrayOf(sectorDashInterval, sectorDashInterval), 0f)
            }

            val path1 = Path()
            path1.moveTo(center.x, center.y)
            path1.lineTo(center.x + sectorDashLength, center.y)
            path1.transform(Matrix().apply { postRotate(sector.startDegrees.toFloat(), center.x , center.y) })
            canvas.drawPath(path1, paint)

            val path2 = Path()
            path2.moveTo(center.x, center.y)
            path2.lineTo(center.x + sectorDashLength, center.y)
            path2.transform(Matrix().apply { postRotate(sector.endDegrees.toFloat(), center.x , center.y) })
            canvas.drawPath(path2, paint)
         }

         canvas.drawText(
            "Racon (${beacon.morseLetter()})",
            center.x + arcSize + (context.resources.displayMetrics.density * 2f),
            center.y - (context.resources.displayMetrics.density * 2f),
            Paint().apply {
               color = Color.Black.toArgb()
               textSize = (context.resources.displayMetrics.density * 4)
               typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
         )
      }

      return bitmap
   }
}