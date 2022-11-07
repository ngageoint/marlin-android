package mil.nga.msi.ui.map.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class MapFilterViewModel @Inject constructor(
   private val filterRepository: FilterRepository
): ViewModel() {
   data class DataSourceModel(
      val dataSource: DataSource,
      val numberOfFilters: Int
   )

   private val filterableDataSources = listOf(
      DataSource.ASAM,
      DataSource.MODU,
      DataSource.LIGHT,
      DataSource.PORT,
      DataSource.RADIO_BEACON,
//      DataSource.DGPS_STATION
   )

   val dataSources: LiveData<List<DataSourceModel>> = MediatorLiveData<List<DataSourceModel>>().apply {
      addSource(filterRepository.filters.asLiveData()) { filters ->
         value = filterableDataSources.map { dataSource ->
            DataSourceModel(dataSource, filters[dataSource]?.size ?: 0)
         }
      }
   }
}