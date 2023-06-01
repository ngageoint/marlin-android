package mil.nga.msi.repository.map

import mil.nga.msi.repository.navigationalwarning.NavigationalWarningLocalDataSource
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.NavigationalWarningImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class NavigationalWarningTileRepository @Inject constructor(
   private val localDataSource: NavigationalWarningLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return localDataSource.getNavigationalWarnings(
         minLatitude = minLatitude,
         minLongitude = minLongitude,
         maxLatitude = maxLatitude,
         maxLongitude = maxLongitude
      ).flatMap { warning ->
         warning.getFeatures().map { NavigationalWarningImage(it) }
      }
   }
}