package mil.nga.msi.repository.map

import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.ui.map.overlay.PortImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class PortTileRepository @Inject constructor(
   private val localDataSource: PortLocalDataSource,
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getPorts(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      PortImage(it)
   }
}