package mil.nga.msi.repository.map

import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.overlay.LightImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

open class LightTileRepository @Inject constructor(
   private val localDataSource: LightLocalDataSource,
   private val userPreferencesRepository: UserPreferencesRepository
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ) = localDataSource.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude).map {
         LightImage(
            light = it,
            userPreferencesRepository = userPreferencesRepository
         )
   }
}