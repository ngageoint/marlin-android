package mil.nga.msi.ui.dgpsstation.list

import androidx.lifecycle.ViewModel
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationListItem
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import javax.inject.Inject

sealed class DgpsStationItem {
   class DgpsStation(val dgpsStation: DgpsStationListItem) : DgpsStationItem()
   class Header(val header : String) : DgpsStationItem()
}

@HiltViewModel
class DgpsStationsViewModel @Inject constructor(
   private val repository: DgpsStationRepository
): ViewModel() {
   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ): DgpsStation? {
      return repository.getDgpsStation(volumeNumber, featureNumber)
   }

   val radioBeacons: Flow<PagingData<DgpsStationItem>> = Pager(PagingConfig(pageSize = 20), null) {
      repository.getDgpsStationListItems()
   }.flow
      .map { pagingData ->
         pagingData
            .map { DgpsStationItem.DgpsStation(it) }
            .insertSeparators { item1: DgpsStationItem.DgpsStation?, item2: DgpsStationItem.DgpsStation? ->
               if (item1?.dgpsStation?.sectionHeader != item2?.dgpsStation?.sectionHeader) {
                  DgpsStationItem.Header(item2?.dgpsStation?.sectionHeader!!)
               } else null
            }
      }
}