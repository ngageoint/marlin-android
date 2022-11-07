package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.LightImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

open class LightTileRepository @Inject constructor(
   private val localDataSource: LightLocalDataSource,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      val boundsFilters = MapBoundsFilter.filtersForBounds(
         minLongitude = minLongitude,
         maxLongitude = maxLongitude,
         minLatitude = minLatitude,
         maxLatitude = maxLatitude
      )

      val entry = filterRepository.filters.first()
      val lightFilters = entry[DataSource.LIGHT] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(lightFilters) }

      val query = QueryBuilder("lights", filters).buildQuery()
      return localDataSource.getLights(query).map {
         LightImage(it, userPreferencesRepository)
      }
   }
}