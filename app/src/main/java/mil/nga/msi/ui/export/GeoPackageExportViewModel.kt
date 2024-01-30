package mil.nga.msi.ui.export

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.geopackage.GeoPackageManager
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.AsamFilter
import mil.nga.msi.datasource.filter.DgpsStationFilter
import mil.nga.msi.datasource.filter.LightFilter
import mil.nga.msi.datasource.filter.ModuFilter
import mil.nga.msi.datasource.filter.NavigationalWarningFilter
import mil.nga.msi.datasource.filter.PortFilter
import mil.nga.msi.datasource.filter.RadioBeaconFilter
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.geopackage.export.Export
import mil.nga.msi.geopackage.export.ExportStatus
import mil.nga.msi.geopackage.export.definition.AsamFeature
import mil.nga.msi.geopackage.export.definition.DgpsStationFeature
import mil.nga.msi.geopackage.export.definition.LightFeature
import mil.nga.msi.geopackage.export.definition.ModuFeature
import mil.nga.msi.geopackage.export.definition.NavigationalWarningFeature
import mil.nga.msi.geopackage.export.definition.PortFeature
import mil.nga.msi.geopackage.export.definition.RadioBeaconFeature
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject

sealed class ExportState {
   data object None: ExportState()
   data object Error: ExportState()
   data class Creating(val status: Map<DataSource, ExportStatus>): ExportState()
   data class Complete(val uri: Uri, val status: Map<DataSource, ExportStatus>): ExportState()
}

@HiltViewModel
class GeoPackageExportViewModel @Inject constructor(
   val locationPolicy: LocationPolicy,
   private val application: Application,
   private val geoPackageManager: GeoPackageManager,
   private val asamRepository: AsamRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val lightRepository: LightRepository,
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val moduRepository: ModuRepository,
   private val portRepository: PortRepository,
   private val radioBeaconRepository: RadioBeaconRepository,
   private val filterRepository: FilterRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val commonFilterParameters = listOf(
      FilterParameter(title = "Location", parameter = "location", type = FilterParameterType.LOCATION),
   )

   val filterParameters = mapOf(
      DataSource.ASAM to AsamFilter.parameters,
      DataSource.MODU to ModuFilter.parameters,
      DataSource.LIGHT to LightFilter.parameters,
      DataSource.NAVIGATION_WARNING to NavigationalWarningFilter.parameters,
      DataSource.PORT to PortFilter.parameters,
      DataSource.RADIO_BEACON to RadioBeaconFilter.parameters,
      DataSource.DGPS_STATION to DgpsStationFilter.parameters,
   )

   private val _exportStatus = MutableLiveData<ExportState>()
   val exportState: LiveData<ExportState> = _exportStatus

   val orderedDataSources = combine(
      userPreferencesRepository.tabs,
      userPreferencesRepository.nonTabs
   ) { tabs, nonTabs ->
      tabs + nonTabs
   }.asLiveData()

   private val _dataSources = MutableLiveData<Set<DataSource>>()
   val dataSources: LiveData<Set<DataSource>> = _dataSources

   private val _dataSourceFilters = MutableLiveData<Map<DataSource, List<Filter>>>()
   val dataSourceFilters: LiveData<Map<DataSource, List<Filter>>> = _dataSourceFilters

   private val _commonFilters = MutableLiveData<List<Filter>>()
   val commonFilters: LiveData<List<Filter>> = _commonFilters

   @OptIn(ExperimentalCoroutinesApi::class)
   val counts = combine(dataSources.asFlow(),  commonFilters.asFlow(), dataSourceFilters.asFlow()) { dataSources, commonFilters, dataSourceFilters ->
      Triple(dataSources, commonFilters, dataSourceFilters)
   }.transformLatest { (dataSources, commonFilters, dataSourceFilters) ->
      val counts = dataSources.associateWith { dataSource ->
         val filters = (dataSourceFilters[dataSource] ?: emptyList()) + commonFilters
         when (dataSource) {
            DataSource.ASAM -> asamRepository.count(filters)
            DataSource.DGPS_STATION -> dgpsStationRepository.count(filters)
            DataSource.LIGHT -> lightRepository.count(filters)
            DataSource.NAVIGATION_WARNING -> navigationalWarningRepository.count(filters)
            DataSource.MODU -> moduRepository.count(filters)
            DataSource.PORT -> portRepository.count(filters)
            DataSource.RADIO_BEACON -> radioBeaconRepository.count(filters)
            else -> null
         }
      }

      emit(counts)
   }.asLiveData()

   init {
      viewModelScope.launch {
         _commonFilters.value = emptyList()
         @Suppress("NullSafeMutableLiveData")
         _dataSourceFilters.value = filterRepository.filters.first()
      }
   }

   fun setExport(export: List<ExportDataSource>) {
      viewModelScope.launch {
         export.forEach { dataSource ->
            toggleDataSource(dataSource.dataSource)

            if (dataSource is ExportDataSource.NavigationalWarning && dataSource.navigationArea != null) {
               val filter = Filter(
                  parameter = FilterParameter(
                     title = "Navigation Area",
                     parameter = "navigation_area",
                     type = FilterParameterType.ENUMERATION,
                     enumerationValues = NavigationArea.entries
                  ),
                  comparator = ComparatorType.EQUALS,
                  value = dataSource.navigationArea
               )
               val filters = _dataSourceFilters.value?.toMutableMap() ?: mutableMapOf()
               val dataSourceFilters = filters[DataSource.NAVIGATION_WARNING]?.toMutableList() ?: mutableListOf()
               dataSourceFilters.add(filter)
               filters[DataSource.NAVIGATION_WARNING] = dataSourceFilters
               _dataSourceFilters.value = filters
            }
         }
      }
   }

   fun toggleDataSource(dataSource: DataSource) {
      viewModelScope.launch {
         val dataSources = _dataSources.value?.toMutableSet() ?: mutableSetOf()
         if (dataSources.contains(dataSource)) {
            dataSources.remove(dataSource)
         } else {
            dataSources.add(dataSource)
         }

         val tabs = userPreferencesRepository.tabs.first().toMutableSet().apply {
            addAll(userPreferencesRepository.nonTabs.first())
         }
         val sortedDataSources = dataSources.sortedWith { d1, d2 ->
            tabs.indexOf(d1).compareTo(tabs.indexOf(d2))
         }.toSet()

         _dataSources.value = sortedDataSources
      }
   }

   fun setCommonFilters(filters: List<Filter>) {
      _commonFilters.value = filters
   }

   fun setDataSourceFilters(dataSource: DataSource, dataSourceFilters: List<Filter>) {
      val filters = _dataSourceFilters.value?.toMutableMap() ?: mutableMapOf()
      filters[dataSource] = dataSourceFilters
      _dataSourceFilters.value = filters
   }

   suspend fun createGeoPackage() = withContext(Dispatchers.IO) {
      _exportStatus.postValue(ExportState.Creating(emptyMap()))

      _dataSources.value?.mapNotNull { dataSource ->
         when (dataSource) {
            DataSource.ASAM -> {
               dataSource to asamRepository.getAsams(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { AsamFeature(it) }
            }
            DataSource.DGPS_STATION -> {
               dataSource to dgpsStationRepository.getDgpsStations(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { DgpsStationFeature(it) }
            }
            DataSource.LIGHT -> {
               dataSource to lightRepository.getLights(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { LightFeature(it) }
            }
            DataSource.NAVIGATION_WARNING -> {
               dataSource to navigationalWarningRepository.getNavigationalWarnings(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { NavigationalWarningFeature(it) }
            }
            DataSource.MODU -> {
               dataSource to moduRepository.getModus(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { ModuFeature(it) }
            }
            DataSource.PORT -> {
               dataSource to portRepository.getPorts(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { PortFeature(it) }
            }
            DataSource.RADIO_BEACON -> {
               dataSource to radioBeaconRepository.getRadioBeacons(
                  filters = dataSourceFilters.value?.get(dataSource) ?: emptyList()
               ).map { RadioBeaconFeature(it) }
            }
            else -> null
         }
      }?.toMap()?.let { items ->
         Export(application, geoPackageManager).export(
            items,
            onStatus = { exportStatus ->
               viewModelScope.launch(Dispatchers.Main) {
                  _exportStatus.postValue(ExportState.Creating(exportStatus.toMap().toMutableMap()))
               }
            },
            onError = {
               _exportStatus.postValue(ExportState.Error)
            }
         )?.let { geoPackage ->
            val uri = FileProvider.getUriForFile(application, "${application.packageName}.fileprovider", geoPackage)
            _exportStatus.postValue(ExportState.Complete(uri, emptyMap()))
            uri
         }
      }
   }
}