package mil.nga.msi.ui.sort

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.*
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.SortParameter
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.dgpsstation.DgpsStationRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.port.PortRoute
import mil.nga.msi.ui.radiobeacon.RadioBeaconRoute
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor(
   private val sortRepository: SortRepository,
): ViewModel() {
   private val _dataSource = MutableLiveData<DataSource>()
   fun setDataSource(dataSource: DataSource) {
      _dataSource.value = dataSource
   }

   val title = _dataSource.map { dataSource ->
      when(dataSource) {
         DataSource.ASAM -> AsamRoute.Sort.shortTitle
         DataSource.MODU -> ModuRoute.Sort.shortTitle
         DataSource.LIGHT -> LightRoute.Sort.shortTitle
         DataSource.PORT -> PortRoute.Sort.shortTitle
         DataSource.RADIO_BEACON -> RadioBeaconRoute.Sort.shortTitle
         DataSource.DGPS_STATION -> DgpsStationRoute.Sort.shortTitle
         else -> ""
      }
   }

   val section = _dataSource.switchMap { dataSource ->
      sortRepository.sort.transform { sort ->
         emit(sort[dataSource]?.section ?: false)
      }.asLiveData()
   }

   val sortOptions = _dataSource.map { dataSource ->
      sortParameterMap[dataSource] ?: emptyList()
   }

   val sortParameters = _dataSource.switchMap { dataSource ->
      sortRepository.sort.transform { sort ->
         emit(sort[dataSource]?.parameters ?: emptyList())
      }.asLiveData()
   }

   fun reset(dataSource: DataSource) {
      viewModelScope.launch {
         sortRepository.resetSortParameters(dataSource)
      }
   }

   fun setSection(dataSource: DataSource, section: Boolean) {
      viewModelScope.launch {
         sortRepository.setSection(dataSource, section)
      }
   }

   fun addPrimarySort(dataSource: DataSource, parameter: SortParameter) {
      viewModelScope.launch {
         sortRepository.setSortParameters(dataSource, listOf(parameter))
      }
   }

   fun removePrimarySort(dataSource: DataSource) {
      viewModelScope.launch {
         sortRepository.setSortParameters(dataSource, emptyList())
      }
   }

   fun addSecondarySort(dataSource: DataSource, parameter: SortParameter) {
      viewModelScope.launch {
         val parameters = sortParameters.value?.toMutableList()?.apply {
            if (isNotEmpty()) {
               add(parameter)
               subList(2, size)
            }
         } ?: emptyList()

         sortRepository.setSortParameters(dataSource, parameters)
      }
   }

   fun removeSecondarySort(dataSource: DataSource) {
      viewModelScope.launch {
         val parameters = sortParameters.value?.toMutableList()?.apply {
            removeLastOrNull()
         } ?: emptyList()

         sortRepository.setSortParameters(dataSource, parameters)
      }
   }

   private val sortParameterMap = mutableMapOf<DataSource, List<FilterParameter>>().apply {
      put(DataSource.ASAM, AsamFilter.parameters)
      put(DataSource.MODU, ModuFilter.parameters)
      put(DataSource.LIGHT, LightFilter.parameters)
      put(DataSource.PORT, PortFilter.parameters)
      put(DataSource.RADIO_BEACON, RadioBeaconFilter.parameters)
      put(DataSource.DGPS_STATION, DgpsStationFilter.parameters)
   }
}