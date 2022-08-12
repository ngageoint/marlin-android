package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

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