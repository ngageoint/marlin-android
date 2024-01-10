package mil.nga.msi.repository.map

import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningLocalDataSource
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.NavigationalWarningImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class NavigationalWarningTileRepository @Inject constructor(
   private val key: NavigationalWarningKey,
   private val localDataSource: NavigationalWarningLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {

      return localDataSource.getNavigationalWarning(key)?.let { warning: NavigationalWarning ->
         val warningMinLongitude = warning.minLongitude
         val warningMaxLongitude = warning.maxLongitude
         val warningMinLatitude = warning.minLatitude
         val warningMaxLatitude = warning.maxLatitude

         if ((warningMinLongitude != null && warningMinLongitude <= maxLongitude) &&
            (warningMaxLongitude != null && warningMaxLongitude >= minLongitude) &&
            (warningMinLatitude != null && warningMinLatitude <= maxLatitude) &&
            (warningMaxLatitude != null && warningMaxLatitude >= minLatitude)) {
            warning.getFeatures().map { NavigationalWarningImage(it) }
         } else emptyList()
      } ?: emptyList()
   }
}

class NavigationalWarningsTileRepository @Inject constructor(
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