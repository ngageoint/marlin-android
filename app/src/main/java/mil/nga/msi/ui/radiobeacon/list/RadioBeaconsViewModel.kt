package mil.nga.msi.ui.radiobeacon.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class RadioBeaconListItem {
   class RadioBeaconItem(val radioBeaconWithBookmark: RadioBeaconWithBookmark) : RadioBeaconListItem()
   class HeaderItem(val header : String) : RadioBeaconListItem()
}

@HiltViewModel
class RadioBeaconsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val bookmarkRepository: BookmarkRepository,
   private val beaconRepository: RadioBeaconRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val radioBeacons = combine(
      sortRepository.sort,
      filterRepository.filters,
      bookmarkRepository.observeBookmarks(DataSource.RADIO_BEACON)
   ) { sort, filters, _ ->
      sort to filters
   }.flatMapLatest { (sort, filters) ->
      Pager(PagingConfig(pageSize = 20), null) {
         beaconRepository.observeRadioBeaconListItems(
            sort = sort[DataSource.RADIO_BEACON]?.parameters ?: emptyList(),
            filters = filters[DataSource.RADIO_BEACON] ?: emptyList()
         )
      }.flow.map { pagingData ->
         pagingData
            .map { beacon ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.RADIO_BEACON, beacon.id)
               RadioBeaconListItem.RadioBeaconItem(RadioBeaconWithBookmark(beacon, bookmark))
            }
            .insertSeparators { item1: RadioBeaconListItem.RadioBeaconItem?, item2: RadioBeaconListItem.RadioBeaconItem? ->
               val section = sort[DataSource.RADIO_BEACON]?.section == true
               val primarySortParameter = sort[DataSource.RADIO_BEACON]?.parameters?.firstOrNull()

               if (section && primarySortParameter != null) {
                  header(primarySortParameter, item1, item2)
               } else null
            }
      }
   }.cachedIn(viewModelScope)

   val radioBeaconFilters = filterRepository.filters.map { entry ->
      entry[DataSource.RADIO_BEACON] ?: emptyList()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   private fun header(sort: SortParameter, item1: RadioBeaconListItem.RadioBeaconItem?, item2: RadioBeaconListItem.RadioBeaconItem?): RadioBeaconListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.radioBeaconWithBookmark?.radioBeacon)
      val item2String = parameterToName(sort.parameter.parameter, item2?.radioBeaconWithBookmark?.radioBeacon)

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