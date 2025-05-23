package mil.nga.msi.ui.map.overlay

import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position

class ModuImage(
   val modu: Modu
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
      val images = mutableListOf<Bitmap>()
      val radius = modu.distance
      if(radius != null){
         images.add(circleImage(context, zoom, radius * 1852, mil.nga.sf.Point(modu.longitude,modu.latitude)))
      }
      images.add(pointImage(context, zoom))

      return images
   }
}