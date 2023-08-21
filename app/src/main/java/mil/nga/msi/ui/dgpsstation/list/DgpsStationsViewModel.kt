package mil.nga.msi.ui.dgpsstation.list

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class DgpsStationListItem {
   class DgpsStationItem(val dgpsStationWithBookmark: DgpsStationWithBookmark) : DgpsStationListItem()
   class HeaderItem(val header : String) : DgpsStationListItem()
}

@HiltViewModel
class DgpsStationsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val dgpsStations = combine(
      sortRepository.sort,
      filterRepository.filters,
      bookmarkRepository.observeBookmarks(DataSource.DGPS_STATION)
   ) { sort, filters, _ ->
      sort to filters
   }.flatMapLatest { (sort, filters) ->
      Pager(PagingConfig(pageSize = 20), null) {
         dgpsStationRepository.observeDgpsStationListItems(
            sort = sort[DataSource.DGPS_STATION]?.parameters ?: emptyList(),
            filters = filters[DataSource.DGPS_STATION] ?: emptyList()
         )
      }.flow.map { pagingData ->
         pagingData
            .map { dgpsStation ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.DGPS_STATION, dgpsStation.id)
               DgpsStationListItem.DgpsStationItem(DgpsStationWithBookmark(dgpsStation, bookmark))
            }
            .insertSeparators { item1: DgpsStationListItem.DgpsStationItem?, item2: DgpsStationListItem.DgpsStationItem? ->
               val section = sort[DataSource.DGPS_STATION]?.section == true
               val primarySortParameter = sort[DataSource.DGPS_STATION]?.parameters?.firstOrNull()

               if (section && primarySortParameter != null) {
                  header(primarySortParameter, item1, item2)
               } else null
            }
      }
   }.cachedIn(viewModelScope)

   val dgpsStationFilters = filterRepository.filters.map { entry ->
      entry[DataSource.DGPS_STATION] ?: emptyList()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   private fun header(sort: SortParameter, item1: DgpsStationListItem.DgpsStationItem?, item2: DgpsStationListItem.DgpsStationItem?): DgpsStationListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.dgpsStationWithBookmark?.dgpsStation)
      val item2String = parameterToName(sort.parameter.parameter, item2?.dgpsStationWithBookmark?.dgpsStation)

      return if (item1String == null && item2String != null) {
         DgpsStationListItem.HeaderItem(item2String)
      } else if (item1String != null && item2String != null && item1String != item2String) {
         DgpsStationListItem.HeaderItem(item2String)
      } else null
   }

   private fun parameterToName(parameter: String, dgpsStation: DgpsStation?): String? {
      return when(parameter) {
         "name" -> "${dgpsStation?.name}"
         "location" -> "${dgpsStation?.latitude} ${dgpsStation?.longitude}"
         "latitude" -> "${dgpsStation?.latitude}"
         "longitude" -> "${dgpsStation?.longitude}"
         "feature_number" -> "${dgpsStation?.featureNumber}"
         "geopolitical_heading" -> "${dgpsStation?.geopoliticalHeading}"
         "station_id" -> "${dgpsStation?.stationId}"
         "range" -> "${dgpsStation?.range}"
         "frequency" -> "${dgpsStation?.frequency}"
         "transfer_rate" -> "${dgpsStation?.transferRate}"
         "remarks" -> "${dgpsStation?.remarks}"
         "notice_number" -> "${dgpsStation?.noticeNumber}"
         "notice_week" -> "${dgpsStation?.noticeWeek}"
         "notice_year" -> "${dgpsStation?.noticeYear}"
         "volume_number" -> "${dgpsStation?.volumeNumber}"
         "preceding_note" -> "${dgpsStation?.precedingNote}"
         "post_note" -> "${dgpsStation?.postNote}"
         "aid_type" -> "${dgpsStation?.aidType}"
         "region_heading" -> "${dgpsStation?.regionHeading}"
         "remove_from_list" -> "${dgpsStation?.removeFromList}"
         "delete_flag" -> "${dgpsStation?.deleteFlag}"
         else -> null
      }
   }
}