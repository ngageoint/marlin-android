package mil.nga.msi.ui.export

import android.app.Application
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.geopackage.GeoPackageManager
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.Filter
import mil.nga.msi.geopackage.export.AsamFeature
import mil.nga.msi.geopackage.export.Export
import mil.nga.msi.geopackage.export.ExportStatus
import mil.nga.msi.geopackage.export.Feature
import mil.nga.msi.geopackage.export.ModuFeature
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
   object None: ExportState()
   data class Creating(val status: Map<DataSource, ExportStatus>): ExportState()
   data class Complete(val status: Map<DataSource, ExportStatus>): ExportState()
}

@HiltViewModel
class GeoPackageExportViewModel @Inject constructor(
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
   private val _exportStatus = MutableLiveData<ExportState>()
   val exportState: LiveData<ExportState> = _exportStatus

   private val _dataSources = MutableLiveData<Set<DataSource>>()
   val dataSources: LiveData<Set<DataSource>> = _dataSources

   private val _filters = MutableLiveData<Map<DataSource, List<Filter>>>()
   val filters: LiveData<Map<DataSource, List<Filter>>> = _filters

   private val _counts = MutableLiveData<Map<DataSource, Int>>()
   val counts: LiveData<Map<DataSource, Int>> = _counts

   init {
      viewModelScope.launch {
         _filters.value = filterRepository.filters.first()
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

         _dataSources.postValue(sortedDataSources)

         _counts.value = dataSources.associateWith { dataSource ->
            val dataSourceFilters = filters.value?.get(dataSource) ?: emptyList()
            when (dataSource) {
               DataSource.ASAM -> { asamRepository.count(dataSourceFilters) }
               DataSource.DGPS_STATION -> { dgpsStationRepository.count(dataSourceFilters) }
               DataSource.LIGHT -> { lightRepository.count(dataSourceFilters) }
               DataSource.NAVIGATION_WARNING -> { navigationalWarningRepository.count(dataSourceFilters) }
               DataSource.MODU -> { moduRepository.count(dataSourceFilters) }
               DataSource.PORT -> { portRepository.count(dataSourceFilters) }
               DataSource.RADIO_BEACON -> { radioBeaconRepository.count(dataSourceFilters) }
               else -> 0
            }
         }
      }
   }

   suspend fun createGeoPackage() = withContext(Dispatchers.IO) {
      _exportStatus.postValue(ExportState.Creating(emptyMap()))


      val items = mutableMapOf<DataSource, List<Feature>>().apply() {
         val asams = asamRepository.getAsams(
            filters = filters.value?.get(DataSource.ASAM) ?: emptyList()
         ).map { AsamFeature(it) }
         this[DataSource.ASAM] = asams

         val modus = moduRepository.getModus(
            filters = filters.value?.get(DataSource.MODU) ?: emptyList()
         ).map { ModuFeature(it) }
         this[DataSource.MODU] = modus

      }

      Export(application, geoPackageManager).export(
         items,
         onStatus = { exportStatus ->
            viewModelScope.launch(Dispatchers.Main) {
               _exportStatus.value = ExportState.Creating(exportStatus.toMap().toMutableMap())
            }
         }
      )?.let { geoPackage ->
         FileProvider.getUriForFile(application, "${application.packageName}.fileprovider", geoPackage)
      }
   }
}