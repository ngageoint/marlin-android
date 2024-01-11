package mil.nga.msi.ui.navigationalwarning

import android.app.Application
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.NavigationalWarningTileRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningLocalDataSource
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.ui.map.overlay.NavigationalWarningTileProvider
import javax.inject.Inject

@HiltViewModel
class NavigationalWarningViewModel @Inject constructor(
   private val application: Application,
   private val dataSource: NavigationalWarningLocalDataSource,
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   private val keyFlow = MutableSharedFlow<NavigationalWarningKey>(replay = 1)
   fun setWarningKey(key: NavigationalWarningKey) {
      viewModelScope.launch {
         keyFlow.emit(key)
      }
   }

   val tileProvider = keyFlow.map { name ->
      val tileRepository = NavigationalWarningTileRepository(name, dataSource)
      NavigationalWarningTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val warningWithBookmark = keyFlow.flatMapLatest { key ->
      combine(
         navigationalWarningRepository.observeNavigationalWarning(key),
         bookmarkRepository.observeBookmark(DataSource.NAVIGATION_WARNING, key.id())
      ) { warning, bookmark ->
         warning?.let {
            NavigationalWarningWithBookmark(it, bookmark)
         }
      }.filterNotNull().flowOn(Dispatchers.IO)
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}