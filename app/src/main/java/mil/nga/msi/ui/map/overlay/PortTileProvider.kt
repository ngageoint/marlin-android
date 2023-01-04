package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
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
}