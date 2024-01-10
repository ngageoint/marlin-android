package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.map.overlay.AsamImage
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class AsamTileRepository @Inject constructor(
   private val reference: String,
   private val localDataSource: AsamLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return localDataSource.getAsam(reference)?.let { asam ->
         if (asam.latitude in minLatitude..maxLatitude && asam.longitude in minLongitude..maxLongitude) {
            listOf(AsamImage(asam))
         } else emptyList()
      } ?: emptyList()
   }
}

class AsamsTileRepository @Inject constructor(
   private val localDataSource: AsamLocalDataSource,
   private val filterRepository: FilterRepository
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
      val asamFilters = entry[DataSource.ASAM] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(asamFilters) }

      val query = QueryBuilder("asams", filters).buildQuery()
      return localDataSource.getAsams(query).map {
         AsamImage(it)
      }
   }
}