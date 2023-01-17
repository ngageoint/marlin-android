package mil.nga.msi.ui.filter

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.*
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
   val locationPolicy: LocationPolicy,
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
         DataSource.LIGHT -> LightRoute.Filter.shortTitle
         DataSource.PORT -> PortRoute.Filter.shortTitle
         DataSource.RADIO_BEACON -> RadioBeaconRoute.Filter.shortTitle
         DataSource.DGPS_STATION -> DgpsStationRoute.Filter.shortTitle
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
      put(DataSource.ASAM, AsamFilter.parameters)
      put(DataSource.MODU, ModuFilter.parameters)
      put(DataSource.LIGHT, LightFilter.parameters)
      put(DataSource.PORT, PortFilter.parameters)
      put(DataSource.RADIO_BEACON, RadioBeaconFilter.parameters)
      put(DataSource.DGPS_STATION, DgpsStationFilter.parameters)
   }
}