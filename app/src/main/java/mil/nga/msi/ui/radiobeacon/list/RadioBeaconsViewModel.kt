package mil.nga.msi.ui.radiobeacon.list

import androidx.lifecycle.ViewModel
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconListItem
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject

sealed class RadioBeaconItem {
   class RadioBeacon(val radioBeacon: RadioBeaconListItem) : RadioBeaconItem()
   class Header(val header : String) : RadioBeaconItem()
}

@HiltViewModel
class RadioBeaconsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   private val repository: RadioBeaconRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val radioBeacons: Flow<PagingData<RadioBeaconItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.RADIO_BEACON] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observeRadioBeaconListItems(filters)
      }.flow.map { pagingData ->
         pagingData
            .map { RadioBeaconItem.RadioBeacon(it) }
            .insertSeparators { item1: RadioBeaconItem.RadioBeacon?, item2: RadioBeaconItem.RadioBeacon? ->
               if (item1 != null && item2 != null && item1.radioBeacon.sectionHeader != item2.radioBeacon.sectionHeader) {
                  RadioBeaconItem.Header(item2.radioBeacon.sectionHeader)
               } else null
            }
      }
   }

   suspend fun getRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ): RadioBeacon? = repository.getRadioBeacon(volumeNumber, featureNumber)
}