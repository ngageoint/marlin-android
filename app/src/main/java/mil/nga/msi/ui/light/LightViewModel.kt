package mil.nga.msi.ui.light

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.light.LightWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.map.LightTileRepository
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.ui.map.overlay.DataSourceTileProvider
import javax.inject.Inject

@HiltViewModel
class LightViewModel @Inject constructor(
   private val application: Application,
   private val dataSource: LightLocalDataSource,
   private val lightRepository: LightRepository,
   private val mapRepository: MapRepository,
   private val bookmarkRepository: BookmarkRepository,
): ViewModel() {

   private val keyFlow = MutableSharedFlow<LightKey>(replay = 1)
   fun setLightKey(key: LightKey) {
      viewModelScope.launch {
         keyFlow.emit(key)
      }
   }

   val tileProvider = keyFlow.map { key ->
      val tileRepository = LightTileRepository(key, mapRepository, dataSource)
      DataSourceTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val lightState = keyFlow.flatMapLatest { key ->
      combine(
         lightRepository.observeLight(key.volumeNumber, key.featureNumber),
         bookmarkRepository.observeBookmark(DataSource.LIGHT, key.id())
      ) { lights, bookmark ->
         if (lights.isNotEmpty()) {
            LightState(
               lightWithBookmark = LightWithBookmark(lights.first(), bookmark),
               characteristics = lights.drop(0),
            )
         } else null
      }.filterNotNull()
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}