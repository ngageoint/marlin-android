package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import mil.nga.msi.R
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.map.DgpsStationTileRepository
import javax.inject.Inject

class DgpsStationTileProvider @Inject constructor(
   val application: Application,
   val repository: DgpsStationTileRepository
) : DataSourceTileProvider(application, repository)

class DgpsStationImage(
   dgpsStation: DgpsStation
): DataSourceImage {
   override val latitude = dgpsStation.latitude
   override val longitude = dgpsStation.longitude

   override fun image(context: Context, zoom: Int): List<Bitmap> {
      val image =  if (zoom < 13) {
         dgpsStationImage(context)
      } else {
         dgpsStationIconImage(context)
      }

      return listOf(image)
   }

   private fun dgpsStationImage(
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

   private fun dgpsStationIconImage(
      context: Context
   ): Bitmap {
      val size = (context.resources.displayMetrics.density * 24).toInt()
      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      canvas.drawCircle(
         size / 2f,
         size / 2f,
         (size / 2f),
         Paint().apply {
            color = DataSource.DGPS_STATION.color.toArgb()
            style = Paint.Style.FILL
         }
      )

      val iconSize = (context.resources.displayMetrics.density * 20).toInt()
      val icon = AppCompatResources.getDrawable(context, R.drawable.ic_dgps_icon_24)!!
      icon.setBounds(0, 0, iconSize, iconSize)
      canvas.drawBitmap(
         icon.toBitmap(),
         null,
         Rect(size - iconSize, size - iconSize, iconSize, iconSize),
         null
      )

      return bitmap
   }
}