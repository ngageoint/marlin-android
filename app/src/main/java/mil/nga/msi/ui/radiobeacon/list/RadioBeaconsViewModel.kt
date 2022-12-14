package mil.nga.msi.ui.radiobeacon.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class RadioBeaconListItem {
   class RadioBeaconItem(val radioBeacon: RadioBeacon) : RadioBeaconListItem()
   class HeaderItem(val header : String) : RadioBeaconListItem()
}

@HiltViewModel
class RadioBeaconsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val beaconRepository: RadioBeaconRepository
): ViewModel() {

   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.RADIO_BEACON] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.RADIO_BEACON])
      }
   }

   val radioBeacons = Transformations.switchMap(queryParameters) { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         beaconRepository.observeRadioBeaconListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { RadioBeaconListItem.RadioBeaconItem(it) }
         .insertSeparators { item1: RadioBeaconListItem.RadioBeaconItem?, item2: RadioBeaconListItem.RadioBeaconItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val radioBeaconFilters = filterRepository.filters.map { entry ->
      entry[DataSource.RADIO_BEACON] ?: emptyList()
   }.asLiveData()

   suspend fun getRadioBeacon(
      volumeNumber: String,
      featureNumber: String
   ): RadioBeacon? = beaconRepository.getRadioBeacon(volumeNumber, featureNumber)

   private fun header(sort: SortParameter, item1: RadioBeaconListItem.RadioBeaconItem?, item2: RadioBeaconListItem.RadioBeaconItem?): RadioBeaconListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.radioBeacon)
      val item2String = parameterToName(sort.parameter.parameter, item2?.radioBeacon)

      return if (item1String == null && item2String != null) {
         RadioBeaconListItem.HeaderItem(item2String)
      } else if (item1String != null && item2String != null && item1String != item2String) {
         RadioBeaconListItem.HeaderItem(item2String)
      } else null
   }

   private fun parameterToName(parameter: String, radioBeacon: RadioBeacon?): String? {
      return when(parameter) {
         "name" -> "${radioBeacon?.name}"
         "location" -> "${radioBeacon?.latitude} ${radioBeacon?.longitude}"
         "latitude" -> "${radioBeacon?.latitude}"
         "longitude" -> "${radioBeacon?.longitude}"
         "feature_number" -> "${radioBeacon?.featureNumber}"
         "geopolitical_heading" -> "${radioBeacon?.geopoliticalHeading}"
         "range" -> "${radioBeacon?.range}"
         "frequency" -> "${radioBeacon?.frequency}"
         "station_remark" -> "${radioBeacon?.stationRemark}"
         "characteristic" -> "${radioBeacon?.characteristic}"
         "sequence_text" -> "${radioBeacon?.sequenceText}"
         "notice_number" -> "${radioBeacon?.noticeNumber}"
         "notice_week" -> "${radioBeacon?.noticeWeek}"
         "notice_year" -> "${radioBeacon?.noticeYear}"
         "volume_number" -> "${radioBeacon?.volumeNumber}"
         "preceding_note" -> "${radioBeacon?.precedingNote}"
         "post_note" -> "${radioBeacon?.postNote}"
         "aid_type" -> "${radioBeacon?.aidType}"
         "region_heading" -> "${radioBeacon?.regionHeading}"
         "remove_from_list" -> "${radioBeacon?.removeFromList}"
         "delete_flag" -> "${radioBeacon?.deleteFlag}"
         else -> null
      }
   }
}