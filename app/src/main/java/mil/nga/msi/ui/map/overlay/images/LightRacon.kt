package mil.nga.msi.ui.map.overlay.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightColor

fun Light.raconImage(
   context: Context,
   small: Boolean
): Bitmap {
   return if (small) {
      raconImageSmall(context)
   } else {
      raconImageLarge(context)
   }
}

private fun raconImageSmall(
   context: Context
): Bitmap {
   val size = (context.resources.displayMetrics.density * 20).toInt()
   val stroke = (context.resources.displayMetrics.density * 2)
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

private fun raconImageLarge(
   context: Context
): Bitmap {
   val size = (context.resources.displayMetrics.density * 120).toInt()

   val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)

   canvas.drawCircle(
      size / 2f,
      size / 2f,
      size / 4f,
      Paint().apply {
         color = LightColor.RACON.color.toArgb()
         style = Paint.Style.STROKE
         strokeWidth = (context.resources.displayMetrics.density * 4)
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