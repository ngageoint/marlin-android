package mil.nga.msi.repository.map

import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.ui.map.overlay.DgpsStationTile
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class DgpsStationTileRepository @Inject constructor(
   private val localDataSource: DgpsStationLocalDataSource,
): TileRepository {
   override fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getDgpsStations(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      DgpsStationTile(it)
   }
}