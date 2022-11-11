package mil.nga.msi.ui.dgpsstation.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class DgpsStationListItem {
   class DgpsStationItem(val dgpsStation: DgpsStation) : DgpsStationListItem()
   class HeaderItem(val header : String) : DgpsStationListItem()
}

@HiltViewModel
class DgpsStationsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val dgpsStationRepository: DgpsStationRepository
): ViewModel() {

   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.DGPS_STATION] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.DGPS_STATION])
      }
   }

   val dgpsStations = Transformations.switchMap(queryParameters) { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         dgpsStationRepository.observeDgpsStationListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { DgpsStationListItem.DgpsStationItem(it) }
         .insertSeparators { item1: DgpsStationListItem.DgpsStationItem?, item2: DgpsStationListItem.DgpsStationItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val dgpsStationFilters = filterRepository.filters.map { entry ->
      entry[DataSource.DGPS_STATION] ?: emptyList()
   }.asLiveData()

   suspend fun getDgpsStation(
      volumeNumber: String,
      featureNumber: Float
   ): DgpsStation? = dgpsStationRepository.getDgpsStation(volumeNumber, featureNumber)

   private fun header(sort: SortParameter, item1: DgpsStationListItem.DgpsStationItem?, item2: DgpsStationListItem.DgpsStationItem?): DgpsStationListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.dgpsStation)
      val item2String = parameterToName(sort.parameter.parameter, item2?.dgpsStation)

      return if (item1String == null && item2String != null) {
         DgpsStationListItem.HeaderItem(item2String)
      } else if (item2String == null && item1String != null) {
         DgpsStationListItem.HeaderItem(item1String)
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