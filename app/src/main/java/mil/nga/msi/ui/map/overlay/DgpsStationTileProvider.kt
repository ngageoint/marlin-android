package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
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
   override val dataSource = DataSource.DGPS_STATION

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      return listOf(circleImage(context, zoom))
   }
}