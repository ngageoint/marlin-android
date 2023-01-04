package mil.nga.msi.ui.light.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.sort.Sort
import mil.nga.msi.sort.SortParameter
import javax.inject.Inject

sealed class LightListItem {
   class LightItem(val light : Light) : LightListItem()
   class HeaderItem(val header : String) : LightListItem()
}

@HiltViewModel
class LightsViewModel @Inject constructor(
   private val lightRepository: LightRepository,
   filterRepository: FilterRepository,
   sortRepository: SortRepository
): ViewModel() {
   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
      addSource(filterRepository.filters.asLiveData()) { entry ->
         val filters = entry[DataSource.LIGHT] ?: emptyList()
         value = Pair(filters, value?.second)
      }

      addSource(sortRepository.sort.asLiveData()) { entry ->
         val filters = value?.first ?: emptyList()
         value = Pair(filters, entry[DataSource.LIGHT])
      }
   }

   val lights = Transformations.switchMap(queryParameters) { pair ->
      val filters = pair.first
      val sort = pair.second
      Pager(PagingConfig(pageSize = 20), null) {
         lightRepository.observeLightListItems(filters, sort?.parameters ?: emptyList())
      }.liveData
   }.asFlow().map { pagingData ->
      pagingData
         .map { LightListItem.LightItem(it) }
         .insertSeparators { item1: LightListItem.LightItem?, item2: LightListItem.LightItem? ->
            val section = queryParameters.value?.second?.section ?: false
            val primarySortParameter = queryParameters.value?.second?.parameters?.firstOrNull()

            if (section && primarySortParameter != null) {
               header(primarySortParameter, item1, item2)
            } else null
         }
   }.cachedIn(viewModelScope)

   val lightFilters = filterRepository.filters.map { entry ->
      entry[DataSource.LIGHT] ?: emptyList()
   }.asLiveData()

   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ): Light? = lightRepository.getLight(volumeNumber, featureNumber, characteristicNumber)


   suspend fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
   ) = withContext(Dispatchers.IO) {
      lightRepository.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber = 1)
   }

   private fun header(sort: SortParameter, item1: LightListItem.LightItem?, item2: LightListItem.LightItem?): LightListItem.HeaderItem? {
      val item1String = parameterToName(sort.parameter.parameter, item1?.light)
      val item2String = parameterToName(sort.parameter.parameter, item2?.light)

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