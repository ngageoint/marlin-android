package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.map.PortTileRepository
import javax.inject.Inject

class PortTileProvider @Inject constructor(
   val application: Application,
   val repository: PortTileRepository
) : DataSourceTileProvider(application, repository)

class PortTile(
   private val port: Port
): Tileable {
   override val latitude = port.latitude
   override val longitude = port.longitude

   override fun tile(context: Context, zoom: Int): List<Bitmap> {
      return listOf(portTile(context))
   }

   private fun portTile(
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
            color = DataSource.PORT.color.toArgb()
            style = Paint.Style.FILL
            strokeWidth = stroke
         }
      )

      return bitmap
   }
}