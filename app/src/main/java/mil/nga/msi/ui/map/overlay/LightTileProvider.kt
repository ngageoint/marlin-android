package mil.nga.msi.ui.map.overlay

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.google.maps.android.geometry.Bounds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.map.LightTileRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.overlay.images.*
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.Geometry
import mil.nga.sf.geojson.Point
import mil.nga.sf.geojson.Position
import javax.inject.Inject

class LightTileProvider @Inject constructor(
   val application: Application,
   repository: LightTileRepository,
) : DataSourceTileProvider(application, repository)

class LightImage(
   private val light: Light,
   private val userPreferencesRepository: UserPreferencesRepository
): DataSourceImage {
   override val dataSource = DataSource.LIGHT
   override val feature: Feature =
      Feature(
         Point(
            Position(light.longitude, light.latitude)
         )
      )

   override fun image(
      context: Context,
      zoom: Int,
      tileBounds: Bounds,
      tileSize: Double
   ): List<Bitmap> {
      val showLightRanges = runBlocking {
         userPreferencesRepository.showLightRanges.first()
      }

      val showSectorLightRanges = runBlocking {
         userPreferencesRepository.showSectorLightRanges.first()
      }

      val images = mutableListOf<Bitmap>()

      if (light.isFogSignal()) {
         images.add(fogSignal(context))
      }

      if (light.isBuoy()) {
         images.add(buoyImage(context))
      }

      val sectors = light.lightSectors
      val colors = light.lightColors
      if (sectors.isNotEmpty()) {
         val image = if (showSectorLightRanges) {
            sectorRangeImage(light, sectors, tileBounds, tileSize)
         } else {
            sectorImage(context, sectors, zoom < 8)
         }

         image.let { images.add(it) }
      } else if(colors.isNotEmpty()) {
         val image = if (showLightRanges) {
            colorRangeImage(context, light, colors, zoom, tileBounds, tileSize)
         } else {
            colorImage(context, colors, zoom < 13)
         }

         image?.let { images.add(it) }
      } else {
         images.add(raconImage(context, zoom < 13))
      }

      return images
   }
}