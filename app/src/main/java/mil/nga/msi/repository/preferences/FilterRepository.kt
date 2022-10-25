package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.ComparatorType
import mil.nga.msi.type.Filter
import mil.nga.msi.type.FilterParameter
import mil.nga.msi.type.Filters
import mil.nga.msi.type.UserPreferences
import mil.nga.msi.ui.asam.filter.AsamFilter
import mil.nga.msi.ui.asam.filter.AsamParameter
import mil.nga.msi.ui.asam.filter.ParameterType
import javax.inject.Inject

class FilterRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val filters: Flow<Map<DataSource, List<AsamFilter>>> = preferencesDataStore.data.map { preferences ->
      preferences.filtersMap.map { entry ->
         val deserialized = entry.value.filtersList.map { deserializeFilter(it) }
         DataSource.valueOf(entry.key) to deserialized
      }.toMap()
   }.distinctUntilChanged()

   suspend fun setFilter(dataSource: DataSource, filters: List<AsamFilter>) {
      preferencesDataStore.updateData { preferences ->
         val builder = preferences.toBuilder()
         val filterBuilder = builder.filtersMap[dataSource.name]?.toBuilder() ?: Filters.newBuilder()
         val newFilters = filterBuilder
            .clearFilters()
            .addAllFilters(filters.map { serializeFilter(it) })
            .build()

         builder.putFilters(dataSource.name, newFilters)

         builder.build()
      }
   }

   private fun serializeFilter(filter: AsamFilter): Filter {
      val parameter = FilterParameter.newBuilder()
         .setType(filter.parameter.type.name)
         .setName(filter.parameter.name)
         .setTitle(filter.parameter.title)
         .build()

      return Filter.newBuilder()
         .setParameter(parameter)
         .setComparator(filter.comparator.name)
         .setValue(filter.value.toString())
         .build()
   }

   private fun deserializeFilter(filter: Filter): AsamFilter {
      val parameter = AsamParameter(
         type = ParameterType.valueOf(filter.parameter.type),
         title = filter.parameter.title,
         name = filter.parameter.name
      )

      return AsamFilter(
         parameter = parameter,
         comparator = ComparatorType.valueOf(filter.comparator),
         value = filter.value
      )
   }
}