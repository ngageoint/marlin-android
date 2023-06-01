package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.map.ModuTileRepository
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Geometry
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position
import javax.inject.Inject

class ModuTileProvider @Inject constructor(
   val application: Application,
   val repository: ModuTileRepository
) : DataSourceTileProvider(application, repository)

class ModuImage(
   modu: Modu
): DataSourceImage {
   override val dataSource = DataSource.MODU
   override val feature: Feature =
      Feature(
         Point(
            Position(modu.longitude, modu.latitude)
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