package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.DgpsStationImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class DgpsStationTileRepository @Inject constructor(
   private val key: DgpsStationKey,
   private val localDataSource: DgpsStationLocalDataSource
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      return localDataSource.getDgpsStation(
         volumeNumber = key.volumeNumber, featureNumber = key.featureNumber
      )?.let { dgpsStation ->
         if (dgpsStation.latitude in minLatitude..maxLatitude && dgpsStation.longitude in minLongitude..maxLongitude) {
            listOf(DgpsStationImage(dgpsStation))
         } else emptyList()
      } ?: emptyList()
   }
}

class DgpsStationsTileRepository @Inject constructor(
   private val localDataSource: DgpsStationLocalDataSource,
   private val filterRepository: FilterRepository,
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
      val dgpsFilters = entry[DataSource.DGPS_STATION] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(dgpsFilters) }

      val query = QueryBuilder("dgps_stations", filters).buildQuery()
      return localDataSource.getDgpsStations(query).map {
         DgpsStationImage(it)
      }
   }
}