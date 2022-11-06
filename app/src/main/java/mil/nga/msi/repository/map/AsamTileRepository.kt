package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
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

      val filtersWithBounds = filters.toMutableList().apply {
         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Min Latitude",
                  parameter =  "latitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLatitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Min Longitude",
                  parameter =  "longitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLongitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Max Latitude",
                  parameter =  "latitude",
               ),
               comparator = ComparatorType.LESS_THAN_OR_EQUAL,
               value = maxLatitude
            )
         )

         add(
            Filter(
               parameter = FilterParameter(
                  type = FilterParameterType.DOUBLE,
                  title = "Max Longitude",
                  parameter =  "longitude",
               ),
               comparator = ComparatorType.LESS_THAN_OR_EQUAL,
               value = maxLongitude
            )
         )
      }

      val query = QueryBuilder("asams", filtersWithBounds).buildQuery()
      return localDataSource.getAsams(query).map {
         AsamImage(it)
      }
   }
}