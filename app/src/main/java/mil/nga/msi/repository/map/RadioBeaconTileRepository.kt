package mil.nga.msi.repository.map

import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import mil.nga.msi.ui.map.overlay.RadioBeaconImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class RadioBeaconTileRepository @Inject constructor(
   private val localDataSource: RadioBeaconLocalDataSource,
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getRadioBeacons(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
      RadioBeaconImage(it)
   }
}