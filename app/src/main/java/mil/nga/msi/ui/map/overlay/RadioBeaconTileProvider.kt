package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.map.RadioBeaconTileRepository
import javax.inject.Inject

class RadioBeaconTileProvider @Inject constructor(
   val application: Application,
   val repository: RadioBeaconTileRepository
) : DataSourceTileProvider(application, repository)

class RadioBeaconImage(
   private val beacon: RadioBeacon
): DataSourceImage {
   override val latitude = beacon.latitude
   override val longitude = beacon.longitude
   override val dataSource = DataSource.RADIO_BEACON

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      val image = if (zoom < 13) {
         radioBeaconSectorImageSmall(context)
      } else {
         sectorImageLarge(context, beacon)
      }

      return listOf(image)
   }

   private fun radioBeaconSectorImageSmall(
      context: Context
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 24).toInt()
      val stroke = (context.resources.displayMetrics.density * 4)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = DataSource.RADIO_BEACON.color.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = stroke
         }
      )

      return bitmap
   }

   private fun sectorImageLarge(
      context: Context,
      beacon: RadioBeacon
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 256).toInt()
      val stroke = (context.resources.displayMetrics.density * 6).toInt()
      val center = PointF(size / 2f, size / 2f)
      val arcSize = size / 2

      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

      beacon.azimuthCoverage().forEach { sector ->
         val canvas = Canvas(bitmap)

         canvas.drawCircle(
            size / 2f,
            size / 2f,
            (context.resources.displayMetrics.density * 4),
            Paint().apply {
               color = Color.Black.toArgb()
               style = Paint.Style.STROKE
               strokeWidth = (context.resources.displayMetrics.density * 2)
            }
         )

         canvas.drawCircle(
            size / 2f,
            size / 2f,
            (context.resources.displayMetrics.density * 1f),
            Paint().apply {
               color = Color.Black.toArgb()
               style = Paint.Style.FILL
               strokeWidth = (context.resources.displayMetrics.density * 1)
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

         val sectorDashLength = (context.resources.displayMetrics.density * 128).toInt()
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

         canvas.drawText("Racon (${beacon.morseLetter()})", center.x + (size / 4f) + (context.resources.displayMetrics.density * 4), center.y, Paint().apply {
            color = Color.Black.toArgb()
            textSize = (context.resources.displayMetrics.density * 12)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
         })
      }

      return bitmap
   }
}