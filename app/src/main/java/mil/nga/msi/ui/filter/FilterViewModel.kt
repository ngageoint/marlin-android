package mil.nga.msi.ui.filter

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.modu.ModuRoute
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
   private val filterRepository: FilterRepository,
): ViewModel() {
   private val _dataSource = MutableLiveData<DataSource>()
   fun setDataSource(dataSource: DataSource) {
      _dataSource.value = dataSource
   }

   val title = Transformations.map(_dataSource) { dataSource ->
      when(dataSource) {
         DataSource.ASAM -> AsamRoute.Filter.shortTitle
         DataSource.MODU -> ModuRoute.Filter.shortTitle
         else -> ""
      }
   }

   val filterParameters = Transformations.map(_dataSource) { dataSource ->
      filterParameterMap[dataSource] ?: emptyList()
   }

   val filters = Transformations.switchMap(_dataSource) { dataSource ->
      filterRepository.filters.transform { filters ->
         emit(filters[dataSource] ?: emptyList())
      }.asLiveData()
   }

   fun setFilters(dataSource: DataSource, filters: List<Filter>) {
      viewModelScope.launch {
         filterRepository.setFilter(dataSource, filters)
      }
   }

   private val filterParameterMap = mutableMapOf<DataSource, List<FilterParameter>>().apply {
      put(DataSource.ASAM, listOf(
         FilterParameter(title = "Date", name = "date", type = FilterParameterType.DATE),
         FilterParameter(title = "Location", name = "date", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Reference", name = "reference", type = FilterParameterType.STRING),
         FilterParameter(title = "Latitude", name = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", name = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Navigation Area", name = "navigation_area", type = FilterParameterType.STRING),
         FilterParameter(title = "Subregion", name = "subregion", type = FilterParameterType.STRING),
         FilterParameter(title = "Description", name = "description", type = FilterParameterType.STRING),
         FilterParameter(title = "Hostility", name = "hostility", type = FilterParameterType.STRING),
         FilterParameter(title = "Victim", name ="victim", type = FilterParameterType.STRING))
      )

      put(DataSource.MODU, listOf(
         FilterParameter(title = "Date", name ="date", type = FilterParameterType.DATE),
         FilterParameter(title = "Name", name ="name", type = FilterParameterType.STRING),
         FilterParameter(title = "Location", name = "location", type = FilterParameterType.LOCATION),
         FilterParameter(title = "Latitude", name = "latitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Longitude", name = "longitude", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Region", name = "region", type = FilterParameterType.STRING),
         FilterParameter(title = "Subregion", name = "subregion", type = FilterParameterType.STRING),
         FilterParameter(title = "Distance", name = "distance", type = FilterParameterType.DOUBLE),
         FilterParameter(title = "Special Status", name = "special_status", type = FilterParameterType.STRING),
         FilterParameter(title = "Rig Status", name = "rig_status", type = FilterParameterType.STRING),
         FilterParameter(title = "Navigation Area", name = "navigation_area", type = FilterParameterType.STRING))
      )
   }
}