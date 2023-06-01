package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.map.PortTileRepository
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Geometry
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position
import javax.inject.Inject

class PortTileProvider @Inject constructor(
   val application: Application,
   val repository: PortTileRepository
) : DataSourceTileProvider(application, repository)

class PortImage(
   port: Port
): DataSourceImage {
   override val dataSource = DataSource.PORT
   override val feature: Feature =
      Feature(
         Point(
            Position(port.longitude, port.latitude)
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