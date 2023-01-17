package mil.nga.msi.ui.image

import android.graphics.*

fun Bitmap.tint(color: Int): Bitmap {
   val paint = Paint()
   paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
   val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
   val canvas = Canvas(bitmap)
   canvas.drawBitmap(this, 0f, 0f, paint)
   return bitmap
}