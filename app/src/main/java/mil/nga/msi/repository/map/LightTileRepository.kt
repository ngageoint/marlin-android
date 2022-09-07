package mil.nga.msi.repository.map

import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.ui.map.overlay.LightImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class LightTileRepository @Inject constructor(
   private val localDataSource: LightLocalDataSource,
): TileRepository {
   override fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      LightImage(it)
   }
}