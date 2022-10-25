package mil.nga.msi.repository.map

import kotlinx.coroutines.flow.first
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.ComparatorType
import mil.nga.msi.datasource.filter.QueryBuilder
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.asam.filter.AsamFilter
import mil.nga.msi.ui.asam.filter.AsamParameter
import mil.nga.msi.ui.asam.filter.ParameterType
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
            AsamFilter(
               parameter = AsamParameter(
                  type = ParameterType.DOUBLE,
                  title = "Min Latitude",
                  name =  "latitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLatitude
            )
         )

         add(
            AsamFilter(
               parameter = AsamParameter(
                  type = ParameterType.DOUBLE,
                  title = "Min Longitude",
                  name =  "longitude",
               ),
               comparator = ComparatorType.GREATER_THAN_OR_EQUAL,
               value = minLongitude
            )
         )

         add(
            AsamFilter(
               parameter = AsamParameter(
                  type = ParameterType.DOUBLE,
                  title = "Max Latitude",
                  name =  "latitude",
               ),
               comparator = ComparatorType.LESS_THAN_OR_EQUAL,
               value = maxLatitude
            )
         )

         add(
            AsamFilter(
               parameter = AsamParameter(
                  type = ParameterType.DOUBLE,
                  title = "Max Longitude",
                  name =  "longitude",
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