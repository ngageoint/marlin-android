package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.map.LightTileRepository
import mil.nga.msi.ui.map.overlay.images.*
import javax.inject.Inject

class LightTileProvider @Inject constructor(
   val application: Application,
   val repository: LightTileRepository
) : DataSourceTileProvider(application, repository)

class LightImage(
   private val light: Light
): DataSourceImage {
   override val latitude = light.latitude
   override val longitude = light.longitude
   override val dataSource = DataSource.LIGHT

   override fun image(context: Context, zoom: Int): List<Bitmap> {
      val images = mutableListOf<Bitmap>()

      if (light.isFogSignal()) {
         images.add(fogSignal(context))
      }

      if (light.isBuoy()) {
         images.add(buoyImage(context))
      }

      val small = zoom < 13
      val sectors = light.lightSectors()
      val colors = light.lightColors()
      if (sectors.isNotEmpty()) {
         images.add(sectorImage(context, sectors, small))
      } else if(colors.isNotEmpty()) {
         images.add(colorImage(context, colors, small))
      } else {
         images.add(raconImage(context, small))
      }

      return images
   }
}