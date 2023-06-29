package mil.nga.msi.ui.light

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.light.LightsWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LightViewModel @Inject constructor(
   private val lightRepository: LightRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("lightTileProvider") val tileProvider: TileProvider
): ViewModel() {

   private val _lightKeyFlow = MutableSharedFlow<LightKey>(replay = 1)
   fun setLightKey(key: LightKey) {
      viewModelScope.launch {
         _lightKeyFlow.emit(key)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val lightsWithBookmark = _lightKeyFlow.flatMapLatest { key ->
      combine(
         lightRepository.observeLight(key.volumeNumber, key.featureNumber),
         bookmarkRepository.observeBookmark(DataSource.LIGHT, key.id())
      ) { light, bookmark ->
         LightsWithBookmark(light, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}