package mil.nga.msi.repository.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.di.DataStoreModule
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.sort.SortDirection
import mil.nga.msi.type.Sort
import mil.nga.msi.type.SortParameter
import mil.nga.msi.type.UserPreferences
import javax.inject.Inject

class SortRepository @Inject constructor(
   private val preferencesDataStore: DataStore<UserPreferences>
) {
   val sort: Flow<Map<DataSource, mil.nga.msi.sort.Sort>> = preferencesDataStore.data.map { preferences ->
      preferences.sortMap.map { entry ->
         DataSource.valueOf(entry.key) to deserializeSort(entry.value)
      }.toMap()
   }.distinctUntilChanged()

   suspend fun setSection(dataSource: DataSource, section: Boolean) {
      preferencesDataStore.updateData { preferences ->
         val builder = preferences.toBuilder()
         val sortBuilder = builder.sortMap[dataSource.name]?.toBuilder() ?: Sort.newBuilder()
         val newSort = sortBuilder
            .setSection(section)
            .build()

         builder.putSort(dataSource.name, newSort)

         builder.build()
      }
   }

   suspend fun setSortParameters(dataSource: DataSource, parameters: List<mil.nga.msi.sort.SortParameter>) {
      preferencesDataStore.updateData { preferences ->
         val builder = preferences.toBuilder()
         val sortBuilder = builder.sortMap[dataSource.name]?.toBuilder() ?: Sort.newBuilder()
         val newSort = sortBuilder
            .clearList()
            .addAllList(parameters.map { serializeSort(it) })
            .build()

         builder.putSort(dataSource.name, newSort)

         builder.build()
      }
   }

   suspend fun resetSortParameters(dataSource: DataSource) {
      DataStoreModule.sortDefaults[dataSource.name]?.let { sort ->
         setSortParameters(dataSource, deserializeSort(sort).parameters)
      }
   }

   private fun serializeSort(parameter: mil.nga.msi.sort.SortParameter): SortParameter {
      return SortParameter.newBuilder()
         .setType(parameter.parameter.type.name)
         .setName(parameter.parameter.parameter)
         .setTitle(parameter.parameter.title)
         .setDirection(parameter.direction.name)
         .build()
   }

   private fun deserializeSort(sort: Sort): mil.nga.msi.sort.Sort {
      val parameters = sort.listList.map {
         deserializeParameter(it)
      }

      return mil.nga.msi.sort.Sort(
         section = sort.section,
         parameters = parameters
      )
   }

   private fun deserializeParameter(
      sortParameter: SortParameter
   ): mil.nga.msi.sort.SortParameter{
      val parameter = FilterParameter(
         type = FilterParameterType.valueOf(sortParameter.type),
         title = sortParameter.title,
         parameter = sortParameter.name
      )

      return mil.nga.msi.sort.SortParameter(
         parameter = parameter,
         direction = SortDirection.valueOf(sortParameter.direction)
      )
   }
}