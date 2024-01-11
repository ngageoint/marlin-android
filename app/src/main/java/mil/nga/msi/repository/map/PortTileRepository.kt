package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.PortImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class PortTileRepository @Inject constructor(
   private val portNumber: Int,
   private val localDataSource: PortLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return localDataSource.getPort(portNumber)?.let { port ->
         if (port.latitude in minLatitude..maxLatitude && port.longitude in minLongitude..maxLongitude) {
            listOf(PortImage(port))
         } else emptyList()
      } ?: emptyList()
   }
}

class PortsTileRepository @Inject constructor(
   private val localDataSource: PortLocalDataSource,
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
      val portFilters = entry[DataSource.PORT] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(portFilters) }

      val query = QueryBuilder("ports", filters).buildQuery()
      return localDataSource.getPorts(query).map {
         PortImage(it)
      }
   }
}