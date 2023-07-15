package mil.nga.msi.ui.asam.list

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
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.filter.FilterParameterType
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.SortParameter
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class AsamListItem {
   data class AsamItem(val asamWithBookmark : AsamWithBookmark) : AsamListItem()
   data class HeaderItem(val header : String) : AsamListItem()
}

@HiltViewModel
class AsamsViewModel @Inject constructor(
   filterRepository: FilterRepository,
   sortRepository: SortRepository,
   private val asamRepository: AsamRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val asams = combine(
      sortRepository.sort,
      filterRepository.filters,
      bookmarkRepository.observeBookmarks(DataSource.ASAM)
   ) { sort, filters, _ ->
      sort to filters
   }.flatMapLatest { (sort, filters) ->
      Pager(PagingConfig(pageSize = 20), null) {
         asamRepository.observeAsamListItems(
            sort = sort[DataSource.ASAM]?.parameters ?: emptyList(),
            filters = filters[DataSource.ASAM] ?: emptyList()
         )
      }.flow.map { pagingData ->
         pagingData
            .map { asam ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.ASAM, asam.reference)
               AsamListItem.AsamItem(AsamWithBookmark(asam, bookmark))
            }
            .insertSeparators { item1: AsamListItem.AsamItem?, item2: AsamListItem.AsamItem? ->
               val section = sort[DataSource.ASAM]?.section == true
               val primarySortParameter = sort[DataSource.ASAM]?.parameters?.firstOrNull()

               if (section && primarySortParameter != null) {
                  header(primarySortParameter, item1, item2)
               } else null
            }
      }
   }.cachedIn(viewModelScope)

   val asamFilters = filterRepository.filters.map { entry ->
      entry[DataSource.ASAM] ?: emptyList()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   private fun header(sort: SortParameter, item1: AsamListItem.AsamItem?, item2: AsamListItem.AsamItem?): AsamListItem.HeaderItem? {
      return when (sort.parameter.type) {
         FilterParameterType.DATE -> {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
            val date1 = item1?.asamWithBookmark?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date1String = date1?.format(formatter)
            val date2 = item2?.asamWithBookmark?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            val date2String = date2?.format(formatter)

            if (date1String == null && date2String != null) {
               AsamListItem.HeaderItem(date2String)
            } else if (date1String != null && date2String != null && date1String != date2String) {
               AsamListItem.HeaderItem(date2String)
            } else null
         }
         else -> {
            val item1String = parameterToName(sort.parameter.parameter, item1?.asamWithBookmark?.asam)
            val item2String = parameterToName(sort.parameter.parameter, item2?.asamWithBookmark?.asam)

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