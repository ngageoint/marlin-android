package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.MapRepository
import mil.nga.msi.ui.map.MapShape
import javax.inject.Inject

data class NavigationalWarningState(
   val warningWithBookmark: NavigationalWarningWithBookmark,
   val annotations: List<MapShape>
)

@HiltViewModel
class NavigationalWarningViewModel @Inject constructor(
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val bookmarkRepository: BookmarkRepository,
   mapRepository: MapRepository
): ViewModel() {
   val baseMap = mapRepository.baseMapType.asLiveData()

   private val _warningKeyFlow = MutableSharedFlow<NavigationalWarningKey>(replay = 1)
   fun setWarningKey(key: NavigationalWarningKey) {
      viewModelScope.launch {
         _warningKeyFlow.emit(key)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val warningState = _warningKeyFlow.flatMapLatest { key ->
      combine(
         navigationalWarningRepository.observeNavigationalWarning(key),
         bookmarkRepository.observeBookmark(DataSource.NAVIGATION_WARNING, key.id())
      ) { warning, bookmark ->
         warning?.let {
            val annotations = warning.getFeatures().mapNotNull { feature ->
               MapShape.fromGeometry(feature, warning.id)
            }

            NavigationalWarningState(NavigationalWarningWithBookmark(it, bookmark), annotations)
         }
      }.filterNotNull().flowOn(Dispatchers.IO)
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}