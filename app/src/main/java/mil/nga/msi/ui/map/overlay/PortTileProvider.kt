package mil.nga.msi.ui.map.overlay

import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.Port
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position

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