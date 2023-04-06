package mil.nga.msi.ui.asam.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class AsamListItem {
   data class AsamItem(val asam : Asam) : AsamListItem()
   data class HeaderItem(val header : String) : AsamListItem()
}

@HiltViewModel
class AsamsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val asamRepository: AsamRepository,
): ViewModel() {

   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.ASAM] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.ASAM])
      }
   }

   val asams = Transformations.switchMap(queryParameters) { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         asamRepository.observeAsamListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { AsamListItem.AsamItem(it) }
         .insertSeparators { item1: AsamListItem.AsamItem?, item2: AsamListItem.AsamItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val asamFilters = filterRepository.filters.map { entry ->
      entry[DataSource.ASAM] ?: emptyList()
   }.asLiveData()

   suspend fun getAsam(reference: String) = asamRepository.getAsam(reference)

   private fun header(sort: SortParameter, item1: AsamListItem.AsamItem?, item2: AsamListItem.AsamItem?): AsamListItem.HeaderItem? {
      return when (sort.parameter.type) {
         FilterParameterType.DATE -> {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
            val date1 = item1?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date1String = date1?.format(formatter)
            val date2 = item2?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date2String = date2?.format(formatter)

            if (date1String == null && date2String != null) {
               AsamListItem.HeaderItem(date2String)
            } else if (date1String != null && date2String != null && date1String != date2String) {
               AsamListItem.HeaderItem(date2String)
            } else null
         }
         else -> {
            val item1String = parameterToName(sort.parameter.parameter, item1?.asam)
            val item2String = parameterToName(sort.parameter.parameter, item2?.asam)

            if (item1String == null && item2String != null) {
               AsamListItem.HeaderItem(item2String)
            } else if (item1String != null && item2String != null && item1String != item2String) {
               AsamListItem.HeaderItem(item2String)
            } else null
         }
      }
   }

   private fun parameterToName(parameter: String, asam: Asam?): String? {
      return when(parameter) {
         "location" -> "${asam?.latitude} ${asam?.longitude}"
         "reference" -> "${asam?.reference}"
         "latitude" -> "${asam?.latitude}"
         "longitude" -> "${asam?.longitude}"
         "navigation_area" -> "${asam?.navigationArea}"
         "subregion" -> "${asam?.subregion}"
         "description" -> "${asam?.description}"
         "hostility" -> "${asam?.hostility}"
         "victim" -> "${asam?.victim}"
         else -> null
      }
   }
}