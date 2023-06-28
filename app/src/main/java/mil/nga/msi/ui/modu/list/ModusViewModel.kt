package mil.nga.msi.ui.modu.list

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.SortParameter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class ModuListItem {
   data class ModuItem(val moduWithBookmark: ModuWithBookmark) : ModuListItem()
   data class HeaderItem(val header : String) : ModuListItem()
}

@HiltViewModel
class ModusViewModel @Inject constructor(
   private val moduRepository: ModuRepository,
   private val bookmarkRepository: BookmarkRepository,
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val modus = combine(
      sortRepository.sort,
      filterRepository.filters,
      bookmarkRepository.observeBookmarks(DataSource.ASAM)
   ) { sort, filters, _ ->
      sort to filters
   }.flatMapLatest { (sort, filters) ->
      Pager(PagingConfig(pageSize = 20), null) {
         moduRepository.observeModuListItems(
            sort = sort[DataSource.MODU]?.parameters ?: emptyList(),
            filters = filters[DataSource.MODU] ?: emptyList()
         )
      }.flow.map { pagingData ->
         pagingData
            .map { modu ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.MODU, modu.name)
               ModuListItem.ModuItem(ModuWithBookmark(modu, bookmark))
            }
            .insertSeparators { item1: ModuListItem.ModuItem?, item2: ModuListItem.ModuItem? ->
               val section = sort[DataSource.MODU]?.section == true
               val primarySortParameter = sort[DataSource.MODU]?.parameters?.firstOrNull()

               if (section && primarySortParameter != null) {
                  header(primarySortParameter, item1, item2)
               } else null
            }
      }
   }.cachedIn(viewModelScope)

   val moduFilters = filterRepository.filters.map { entry ->
      entry[DataSource.MODU] ?: emptyList()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   private fun header(sort: SortParameter, item1: ModuListItem.ModuItem?, item2: ModuListItem.ModuItem?): ModuListItem.HeaderItem? {
      return when (sort.parameter.type) {
         FilterParameterType.DATE -> {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
            val date1 = item1?.moduWithBookmark?.modu?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date1String = date1?.format(formatter)
            val date2 = item2?.moduWithBookmark?.modu?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date2String = date2?.format(formatter)

            if (date1String == null && date2String != null) {
               ModuListItem.HeaderItem(date2String)
            } else if (date1String != null && date2String != null && date1String != date2String) {
               ModuListItem.HeaderItem(date2String)
            } else null

         }
         else -> {
            val item1String = parameterToName(sort.parameter.parameter, item1?.moduWithBookmark?.modu)
            val item2String = parameterToName(sort.parameter.parameter, item2?.moduWithBookmark?.modu)

            if (item1String == null && item2String != null) {
               ModuListItem.HeaderItem(item2String)
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