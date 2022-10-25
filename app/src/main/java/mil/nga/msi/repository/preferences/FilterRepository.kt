package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.ComparatorType
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.type.Filters
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject

class FilterRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val filters: Flow<Map<DataSource, List<mil.nga.msi.filter.Filter>>> = preferencesDataStore.data.map { preferences ->
      preferences.filtersMap.map { entry ->
         val deserialized = entry.value.filtersList.map { deserializeFilter(it) }
         DataSource.valueOf(entry.key) to deserialized
      }.toMap()
   }.distinctUntilChanged()

   suspend fun setFilter(dataSource: DataSource, filters: List<mil.nga.msi.filter.Filter>) {
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

   private fun serializeFilter(filter: mil.nga.msi.filter.Filter): mil.nga.msi.type.Filter {
      val parameter = mil.nga.msi.type.FilterParameter.newBuilder()
         .setType(filter.parameter.type.name)
         .setName(filter.parameter.name)
         .setTitle(filter.parameter.title)
         .build()

      return mil.nga.msi.type.Filter.newBuilder()
         .setParameter(parameter)
         .setComparator(filter.comparator.name)
         .setValue(filter.value.toString())
         .build()
   }

   private fun deserializeFilter(filter: mil.nga.msi.type.Filter): mil.nga.msi.filter.Filter {
      val parameter = mil.nga.msi.filter.FilterParameter(
         type = FilterParameterType.valueOf(filter.parameter.type),
         title = filter.parameter.title,
         name = filter.parameter.name
      )

      return mil.nga.msi.filter.Filter(
         parameter = parameter,
         comparator = ComparatorType.valueOf(filter.comparator),
         value = filter.value
      )
   }
}