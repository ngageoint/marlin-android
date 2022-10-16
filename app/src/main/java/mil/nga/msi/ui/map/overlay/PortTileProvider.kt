package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.map.PortTileRepository
import javax.inject.Inject

class PortTileProvider @Inject constructor(
   val application: Application,
   val repository: PortTileRepository
) : DataSourceTileProvider(application, repository)

class PortImage(
   port: Port
): DataSourceImage {
   override val latitude = port.latitude
   override val longitude = port.longitude
   override val dataSource = DataSource.PORT

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      return listOf(circleImage(context, zoom))
   }

//   private fun portImage(
//      context: Context
//   ): Bitmap {
//      val size = (context.resources.displayMetrics.density * 12).toInt()
//      val stroke = (context.resources.displayMetrics.density * 2)
//      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
//      val canvas = Canvas(bitmap)
//
//      canvas.drawCircle(
//         size / 2f,
//         size / 2f,
//         (size / 2f) - stroke,
//         Paint().apply {
//            color = DataSource.PORT.color.toArgb()
//            style = Paint.Style.FILL
//            strokeWidth = stroke
//         }
//      )
//
//      return bitmap
//   }
//
//   private fun portIconImage(
//      context: Context
//   ): Bitmap {
//      val size = (context.resources.displayMetrics.density * 24).toInt()
//      val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
//      val canvas = Canvas(bitmap)
//
//      canvas.drawCircle(
//         size / 2f,
//         size / 2f,
//         (size / 2f),
//         Paint().apply {
//            color = DataSource.PORT.color.toArgb()
//            style = Paint.Style.FILL
//         }
//      )
//
//      val iconSize = (context.resources.displayMetrics.density * 20).toInt()
//      val icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_anchor_24)!!
//      icon.setBounds(0, 0, iconSize, iconSize)
//      canvas.drawBitmap(
//         icon.toBitmap(),
//         null,
//         Rect(size - iconSize, size - iconSize, iconSize, iconSize),
//         null
//      )
//
//      return bitmap
//   }
}