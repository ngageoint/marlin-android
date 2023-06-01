package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.map.DgpsStationTileRepository
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Geometry
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position
import javax.inject.Inject

class DgpsStationTileProvider @Inject constructor(
   val application: Application,
   val repository: DgpsStationTileRepository
) : DataSourceTileProvider(application, repository)

class DgpsStationImage(
   dgpsStation: DgpsStation
): DataSourceImage {
   override val dataSource = DataSource.DGPS_STATION
   override val feature: Feature =
      Feature(
         Point(
            Position(dgpsStation.longitude, dgpsStation.latitude)
         )
      )

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      return listOf(pointImage(context, zoom))
   }
}