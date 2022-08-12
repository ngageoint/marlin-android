package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightSector

fun sectorImage(
   context: Context,
   sectors: List<LightSector>,
   small: Boolean
): Bitmap {
   return if (small) {
      sectorImageSmall(context, sectors)
   } else {
      sectorImageLarge(context, sectors, includeLetters = true, includeSectorDashes = true)
   }
}

private fun sectorImageSmall(
   context: Context,
   sectors: List<LightSector>
): Bitmap {
   val size = (context.resources.displayMetrics.density * 16).toInt()
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
   sectors: List<LightSector>,
   includeLetters: Boolean = true,
   includeSectorDashes: Boolean = true
): Bitmap {
   val size = (context.resources.displayMetrics.density * 256).toInt()
   val stroke = (context.resources.displayMetrics.density * 6).toInt()
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
            textSize = (context.resources.displayMetrics.density * 12)
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