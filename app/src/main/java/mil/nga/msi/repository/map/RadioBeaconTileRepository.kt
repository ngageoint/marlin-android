package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.MapBoundsFilter
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.RadioBeaconImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class RadioBeaconTileRepository @Inject constructor(
   private val localDataSource: RadioBeaconLocalDataSource,
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
      val beaconFilters = entry[DataSource.RADIO_BEACON] ?: emptyList()
      val filters = boundsFilters.toMutableList().apply { addAll(beaconFilters) }

      val query = QueryBuilder("radio_beacons", filters).buildQuery()
      return localDataSource.getRadioBeacons(query).map {
         RadioBeaconImage(it)
      }
   }
}