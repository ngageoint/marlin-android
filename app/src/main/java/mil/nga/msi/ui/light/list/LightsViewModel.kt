package mil.nga.msi.ui.light.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class LightListItem {
   class LightItem(val lightWithBookmark: LightWithBookmark) : LightListItem()
   class HeaderItem(val header: String) : LightListItem()
}

@HiltViewModel
class LightsViewModel @Inject constructor(
   private val lightRepository: LightRepository,
   val bookmarkRepository: BookmarkRepository,
   filterRepository: FilterRepository,
   sortRepository: SortRepository
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val lights = combine(
      sortRepository.sort,
      filterRepository.filters,
      bookmarkRepository.observeBookmarks(DataSource.LIGHT)
   ) { sort, filters, _ ->
      sort to filters
   }.flatMapLatest { (sort, filters) ->
      Pager(PagingConfig(pageSize = 20), null) {
         lightRepository.observeLightListItems(
            sort = sort[DataSource.LIGHT]?.parameters ?: emptyList(),
            filters = filters[DataSource.LIGHT] ?: emptyList()
         )
      }.flow.map { pagingData ->
         pagingData
            .map { light ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.LIGHT, light.id)
               LightListItem.LightItem(LightWithBookmark(light, bookmark))
            }
            .insertSeparators { item1: LightListItem.LightItem?, item2: LightListItem.LightItem? ->
               val section = sort[DataSource.LIGHT]?.section == true
               val primarySortParameter = sort[DataSource.LIGHT]?.parameters?.firstOrNull()

               if (section && primarySortParameter != null) {
                  header(primarySortParameter, item1, item2)
               } else null
            }
      }
   }.cachedIn(viewModelScope)

   val lightFilters = filterRepository.filters.map { entry ->
      entry[DataSource.LIGHT] ?: emptyList()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   suspend fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
   ) = withContext(Dispatchers.IO) {
      lightRepository.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber = 1)
   }

   private fun header(sort: SortParameter, item1: LightListItem.LightItem?, item2: LightListItem.LightItem?): LightListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.lightWithBookmark?.light)
      val item2String = parameterToName(sort.parameter.parameter, item2?.lightWithBookmark?.light)

      return if (item1String == null && item2String != null) {
         LightListItem.HeaderItem(item2String)
      } else if (item1String != null && item2String != null && item1String != item2String) {
         LightListItem.HeaderItem(item2String)
      } else null
   }

   private fun parameterToName(parameter: String, light: Light?): String? {
      return when(parameter) {
         "name" -> "${light?.name}"
         "location" -> "${light?.latitude} ${light?.longitude}"
         "latitude" -> "${light?.latitude}"
         "longitude" -> "${light?.longitude}"
         "feature_number" -> "${light?.featureNumber}"
         "volume_number" -> "${light?.volumeNumber}"
         "characteristic" -> "${light?.characteristic}"
         "signal" -> "${light?.characteristic}"
         "characteristic_number" -> "${light?.characteristicNumber}"
         "international_feature" -> "${light?.internationalFeature}"
         "geopolitical_heading" -> "${light?.geopoliticalHeading}"
         "region_heading" -> "${light?.regionHeading}"
         "subregion_heading" -> "${light?.subregionHeading}"
         "local_heading" -> "${light?.localHeading}"
         "structure" -> "${light?.structure}"
         "height_feet" -> "${light?.heightFeet}"
         "height_meters" -> "${light?.heightMeters}"
         "range" -> "${light?.range}"
         "remarks" -> "${light?.remarks}"
         "notice_number" -> "${light?.noticeNumber}"
         "notice_week" -> "${light?.noticeWeek}"
         "notice_year" -> "${light?.noticeYear}"
         "preceding_note" -> "${light?.precedingNote}"
         "post_note" -> "${light?.postNote}"
         "section_header" -> "${light?.sectionHeader}"
         else -> null
      }
   }
}