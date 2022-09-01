package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.map.DgpsStationTileRepository
import javax.inject.Inject

class DgpsStationTileProvider @Inject constructor(
   val application: Application,
   val repository: DgpsStationTileRepository
) : DataSourceTileProvider(application, repository)

class DgpsStationTile(
   private val dgpsStation: DgpsStation
): Tileable {
   override val latitude = dgpsStation.latitude
   override val longitude = dgpsStation.longitude

   override fun tile(context: Context, zoom: Int): List<Bitmap> {
      return if (zoom < 13) {
         listOf(dgpsStationTile(context))
      } else emptyList()
   }

   private fun dgpsStationTile(
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
            color = DataSource.DGPS_STATION.color.toArgb()
            style = Paint.Style.FILL
            strokeWidth = stroke
         }
      )

      return bitmap
   }
}