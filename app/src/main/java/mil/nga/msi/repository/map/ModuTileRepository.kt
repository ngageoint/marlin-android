package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.ModuImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class ModuTileRepository @Inject constructor(
   private val name: String,
   private val localDataSource: ModuLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return localDataSource.getModu(name)?.let { modu ->
         if (modu.latitude in minLatitude..maxLatitude && modu.longitude in minLongitude..maxLongitude) {
            listOf(ModuImage(modu))
         } else emptyList()
      } ?: emptyList()
   }
}

class ModusTileRepository @Inject constructor(
   private val localDataSource: ModuLocalDataSource,
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
      val moduFilters = entry[DataSource.MODU] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(moduFilters) }

      val query = QueryBuilder("modus", filters).buildQuery()
      return localDataSource.getModus(query).map {
         ModuImage(it)
      }
   }
}