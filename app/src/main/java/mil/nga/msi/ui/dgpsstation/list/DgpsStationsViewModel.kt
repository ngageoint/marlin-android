package mil.nga.msi.ui.dgpsstation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationListItem
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

sealed class DgpsStationItem {
   class DgpsStation(val dgpsStation: DgpsStationListItem) : DgpsStationItem()
   class Header(val header : String) : DgpsStationItem()
}

@HiltViewModel
class DgpsStationsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   private val repository: DgpsStationRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val dgpsStations: Flow<PagingData<DgpsStationItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.DGPS_STATION] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observeDgpsStationListItems(filters)
      }.flow.map { pagingData ->
         pagingData
            .map { DgpsStationItem.DgpsStation(it) }
            .insertSeparators { item1: DgpsStationItem.DgpsStation?, item2: DgpsStationItem.DgpsStation? ->
               if (item1 != null && item2 != null && item1.dgpsStation.sectionHeader != item2.dgpsStation.sectionHeader) {
                  DgpsStationItem.Header(item2.dgpsStation.sectionHeader)
               } else null
            }
      }
   }

   val dgpsStationFilters = filterRepository.filters.map { entry ->
      entry[DataSource.DGPS_STATION] ?: emptyList()
   }.asLiveData()

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ): DgpsStation? = repository.getDgpsStation(volumeNumber, featureNumber)
}