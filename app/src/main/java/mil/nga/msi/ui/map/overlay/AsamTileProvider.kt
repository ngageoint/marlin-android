package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.map.AsamTileRepository
import javax.inject.Inject

class AsamTileProvider @Inject constructor(
   val application: Application,
   val repository: AsamTileRepository
) : DataSourceTileProvider(application, repository)

class AsamImage(
   asam: Asam
): DataSourceImage {
   override val latitude = asam.latitude
   override val longitude = asam.longitude
   override val dataSource = DataSource.ASAM

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      return listOf(circleImage(context, zoom))
   }
}