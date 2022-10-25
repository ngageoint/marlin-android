package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.map.overlay.AsamImage
import mil.nga.msi.ui.map.overlay.DataSourceImage
import mil.nga.msi.ui.map.overlay.TileRepository
import javax.inject.Inject

class AsamTileRepository @Inject constructor(
   private val localDataSource: AsamLocalDataSource,
   private val filterRepository: FilterRepository
): TileRepository {
   override suspend fun getTileableItems(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DataSourceImage> {
      val entry = filterRepository.filters.first()
      val filters = entry[DataSource.ASAM] ?: emptyList()
      val query = QueryBuilder("asams", filters).buildQuery()

      return localDataSource.getAsams(query).map {
         AsamImage(it)
      }
   }
}