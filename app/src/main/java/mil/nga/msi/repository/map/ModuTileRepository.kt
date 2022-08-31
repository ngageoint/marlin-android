package mil.nga.msi.repository.map

import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.ui.map.overlay.ModuTile
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class ModuTileRepository @Inject constructor(
   private val localDataSource: ModuLocalDataSource,
): TileRepository {
   override fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getModus(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      ModuTile(it)
   }
}