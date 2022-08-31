package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.map.ModuTileRepository
import mil.nga.msi.repository.preferences.DataSource
import javax.inject.Inject

class ModuTileProvider @Inject constructor(
   val application: Application,
   val repository: ModuTileRepository
) : DataSourceTileProvider(application, repository)

class ModuTile(
   modu: Modu
): Tileable {
   override val latitude = modu.latitude
   override val longitude = modu.longitude

   override fun tile(context: Context, zoom: Int): List<Bitmap> {
      return if (zoom < 13) {
         listOf(asamTile(context))
      } else emptyList()
   }

   private fun asamTile(
      context: Context
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 12).toInt()
      val stroke = (context.resources.displayMetrics.density * 2)
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f) - stroke,
         Paint().apply {
            color = DataSource.MODU.color.toArgb()
            style = Paint.Style.FILL
            strokeWidth = stroke
         }
      )

      return bitmap
   }
}