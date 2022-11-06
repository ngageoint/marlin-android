package mil.nga.msi.ui.light.list

import androidx.lifecycle.ViewModel
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightListItem
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

sealed class LightItem {
   class Light(val light : LightListItem) : LightItem()
   class Header(val header : String) : LightItem()
}

@HiltViewModel
class LightsViewModel @Inject constructor(
   private val repository: LightRepository,
   filterRepository: FilterRepository,
): ViewModel() {

   @OptIn(ExperimentalCoroutinesApi::class)
   val lights: Flow<PagingData<LightItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.LIGHT] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observeLightListItems(filters)
      }.flow.map { pagingData ->
         pagingData
            .map { LightItem.Light(it) }
            .insertSeparators { item1: LightItem.Light?, item2: LightItem.Light? ->
               if (item1 != null && item2 != null && item1.light.sectionHeader != item2.light.sectionHeader) {
                  LightItem.Header(item2.light.sectionHeader)
               } else null
            }
      }
   }

   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ): Light? = repository.getLight(volumeNumber, featureNumber, characteristicNumber)


   suspend fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
   ) = withContext(Dispatchers.IO) {
      repository.getLights(minLatitude, maxLatitude, minLongitude, maxLongitude, characteristicNumber = 1)
   }
}