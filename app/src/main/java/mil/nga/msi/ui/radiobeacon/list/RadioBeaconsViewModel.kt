package mil.nga.msi.ui.radiobeacon.list

import androidx.lifecycle.ViewModel
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconListItem
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject

sealed class RadioBeaconItem {
   class RadioBeacon(val radioBeacon: RadioBeaconListItem) : RadioBeaconItem()
   class Header(val header : String) : RadioBeaconItem()
}

@HiltViewModel
class RadioBeaconsViewModel @Inject constructor(
   private val repository: RadioBeaconRepository
): ViewModel() {

   val radioBeacons: Flow<PagingData<RadioBeaconItem>> = Pager(PagingConfig(pageSize = 20), null) {
      repository.getRadioBeaconListItems()
   }.flow
      .map { pagingData ->
         pagingData
            .map { RadioBeaconItem.RadioBeacon(it) }
            .insertSeparators { item1: RadioBeaconItem.RadioBeacon?, item2: RadioBeaconItem.RadioBeacon? ->
               if (item1?.radioBeacon?.sectionHeader != item2?.radioBeacon?.sectionHeader) {
                  RadioBeaconItem.Header(item2?.radioBeacon?.sectionHeader!!)
               } else null
            }
      }

   suspend fun getRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ): RadioBeacon? = repository.getRadioBeacon(volumeNumber, featureNumber)
}