package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.light.LightColor

fun buoyImage(
   context: Context
): Bitmap {
   val size = (context.resources.displayMetrics.density * 16).toInt()
   val stroke = (context.resources.displayMetrics.density * 2)

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
      (context.resources.displayMetrics.density * 1f),
      Paint().apply {
         color = Color.Black.toArgb()
         style = Paint.Style.FILL
         strokeWidth = (context.resources.displayMetrics.density * 1)
      }
   )

   return bitmap
}