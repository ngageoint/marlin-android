package mil.nga.msi.repository.map

import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.ui.map.overlay.AsamTile
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class AsamTileRepository @Inject constructor(
   private val localDataSource: AsamLocalDataSource,
): TileRepository {
   override fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getAsams(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      AsamTile(it)
   }
}