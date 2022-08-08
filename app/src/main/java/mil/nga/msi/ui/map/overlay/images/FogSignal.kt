package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.light.LightColor

fun fogSignal(
   context: Context
): Bitmap {
   val size = (context.resources.displayMetrics.density * 120).toInt()
   val center = PointF(size / 2f, size / 2f)

   val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)

   val arc1Size = (context.resources.displayMetrics.density * 24)
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
         strokeWidth = (context.resources.displayMetrics.density * 2)
      }
   )

   val arc2Size = (context.resources.displayMetrics.density * 36)
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
         strokeWidth = (context.resources.displayMetrics.density * 2)
      }
   )

   val arc3Size = (context.resources.displayMetrics.density * 48)
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
         strokeWidth = (context.resources.displayMetrics.density * 2)
      }
   )

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

   return bitmap
}