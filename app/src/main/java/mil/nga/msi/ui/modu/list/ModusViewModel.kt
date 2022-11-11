package mil.nga.msi.ui.modu.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.filter.Filter
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class ModuListItem {
   data class ModuItem(val modu : Modu) : ModuListItem()
   data class HeaderItem(val header : String) : ModuListItem()
}

@HiltViewModel
class ModusViewModel @Inject constructor(
   private val moduRepository: ModuRepository,
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
): ViewModel() {
   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.MODU] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.MODU])
      }
   }

   val modus = Transformations.switchMap(queryParameters) { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         moduRepository.observeModuListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { ModuListItem.ModuItem(it) }
         .insertSeparators { item1: ModuListItem.ModuItem?, item2: ModuListItem.ModuItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val moduFilters = filterRepository.filters.map { entry ->
      entry[DataSource.MODU] ?: emptyList()
   }.asLiveData()

   suspend fun getModu(name: String) = moduRepository.getModu(name)

   private fun header(sort: SortParameter, item1: ModuListItem.ModuItem?, item2: ModuListItem.ModuItem?): ModuListItem.HeaderItem? {
      return when (sort.parameter.type) {
         FilterParameterType.DATE -> {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
            val date1 = item1?.modu?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date1String = date1?.format(formatter)
            val date2 = item2?.modu?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date2String = date2?.format(formatter)

            if (date1String == null && date2String != null) {
               ModuListItem.HeaderItem(date2String)
            } else if (date2String == null && date1String != null) {
               ModuListItem.HeaderItem(date1String)
            } else if (date1String != null && date2String != null && date1String != date2String) {
               ModuListItem.HeaderItem(date2String)
            } else null
         }
         else -> {
            val item1String = parameterToName(sort.parameter.parameter, item1?.modu)
            val item2String = parameterToName(sort.parameter.parameter, item2?.modu)

            if (item1String == null && item2String != null) {
               ModuListItem.HeaderItem(item2String)
            } else if (item2String == null && item1String != null) {
               ModuListItem.HeaderItem(item1String)
            } else if (item1String != null && item2String != null && item1String != item2String) {
               ModuListItem.HeaderItem(item2String)
            } else null
         }
      }
   }

   private fun parameterToName(parameter: String, modu: Modu?): String? {
      return when(parameter) {
         "name" -> "${modu?.name}"
         "location" -> "${modu?.latitude} ${modu?.longitude}"
         "latitude" -> "${modu?.latitude}"
         "longitude" -> "${modu?.longitude}"
         "region" -> "${modu?.region}"
         "subregion" -> "${modu?.subregion}"
         "distance" -> "${modu?.distance}"
         "special_status" -> "${modu?.specialStatus}"
         "rig_status" -> "${modu?.rigStatus}"
         "navigation_area" -> "${modu?.navigationArea}"
         else -> null
      }
   }
}