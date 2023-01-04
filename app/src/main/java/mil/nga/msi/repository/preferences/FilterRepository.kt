package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.port.types.EnumerationType
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.type.Filters
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
         .setName(filter.parameter.parameter)
         .setTitle(filter.parameter.title)
         .build()

      return mil.nga.msi.type.Filter.newBuilder()
         .setParameter(parameter)
         .setComparator(filter.comparator.name)
         .setValue(serializeValue(filter.parameter, filter.value))
         .build()
   }

   private fun deserializeFilter(filter: mil.nga.msi.type.Filter): mil.nga.msi.filter.Filter {
      val parameter = FilterParameter(
         type = FilterParameterType.valueOf(filter.parameter.type),
         title = filter.parameter.title,
         parameter = filter.parameter.name
      )

      return mil.nga.msi.filter.Filter(
         parameter = parameter,
         comparator = ComparatorType.valueOf(filter.comparator),
         value = deserializeValue(parameter, filter.value)
      )
   }

   private fun serializeValue(
      parameter: FilterParameter,
      value: Any?
   ): String? {
      return when (parameter.type) {
         FilterParameterType.DATE -> {
            value?.toString() // TODO convert to ISO date
         }
         FilterParameterType.DOUBLE -> value?.toString()
         FilterParameterType.ENUMERATION -> serializeEnumeration(value)
         FilterParameterType.FLOAT -> value?.toString()
         FilterParameterType.INT -> value?.toString()
         FilterParameterType.LOCATION -> value?.toString()
         FilterParameterType.STRING -> value?.toString()
      }
   }

   private fun deserializeValue(
      parameter: FilterParameter,
      value: String?
   ): Any? {
      return when (parameter.type) {
         FilterParameterType.DATE -> value // TODO convert to ISO date
         FilterParameterType.DOUBLE -> value?.toDoubleOrNull()
         FilterParameterType.ENUMERATION -> deserializeEnumeration(value)
         FilterParameterType.FLOAT -> value?.toFloatOrNull()
         FilterParameterType.INT -> value?.toIntOrNull()
         FilterParameterType.LOCATION -> value
         FilterParameterType.STRING -> value
      }
   }

   private fun serializeEnumeration(
      value: Any?
   ): String? {
      return (value as? EnumerationType)?.let { type ->
         EnumerationType.toString(type)
      }
   }

   private fun deserializeEnumeration(
      value: String?
   ): EnumerationType? {
      return EnumerationType.fromString(value)
   }
}