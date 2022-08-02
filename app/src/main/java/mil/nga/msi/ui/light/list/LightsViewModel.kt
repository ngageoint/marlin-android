package mil.nga.msi.ui.light.list

import androidx.lifecycle.ViewModel
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.LightListItem
import mil.nga.msi.repository.light.LightRepository
import javax.inject.Inject

sealed class LightItem {
   class Light(val light : LightListItem) : LightItem()
   class Header(val header : String) : LightItem()
}

@HiltViewModel
class LightsViewModel @Inject constructor(
   private val repository: LightRepository
): ViewModel() {
   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ): Light? {
      return repository.getLight(volumeNumber, featureNumber, characteristicNumber)
   }

   val lights: Flow<PagingData<LightItem>> = Pager(PagingConfig(pageSize = 20), null) {
      repository.getLightListItems()
   }.flow
      .map { pagingData ->
         val mapped = pagingData.map { LightItem.Light(it) }

         mapped.insertSeparators { item1: LightItem.Light?, item2: LightItem.Light? ->
            if (item1?.light?.sectionHeader != item2?.light?.sectionHeader) {
               LightItem.Header(item2?.light?.sectionHeader!!)
            } else null
         }
   }
}