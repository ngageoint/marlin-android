package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position
import javax.inject.Inject

class AsamTileProvider @Inject constructor(
   val application: Application,
   val repository: TileRepository
) : DataSourceTileProvider(application, repository)

class AsamImage(
   asam: Asam
): DataSourceImage {
   override val dataSource = DataSource.ASAM
   override val feature: Feature =
      Feature(
         Point(
            Position(asam.longitude, asam.latitude)
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