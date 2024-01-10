package mil.nga.msi.ui.modu

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.ModuTileRepository
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.ui.map.overlay.ModuTileProvider
import javax.inject.Inject

@HiltViewModel
class ModuViewModel @Inject constructor(
   private val application: Application,
   private val repository: ModuRepository,
   private val dataSource: ModuLocalDataSource,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   private val nameFlow = MutableSharedFlow<String>(replay = 1)
   fun setName(name: String) {
      viewModelScope.launch {
         nameFlow.emit(name)
      }
   }

   val tileProvider = nameFlow.map { name ->
      val tileRepository = ModuTileRepository(name, dataSource)
      ModuTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val moduWithBookmark = nameFlow.flatMapLatest { name ->
      combine(
         repository.observeModu(name),
         bookmarkRepository.observeBookmark(DataSource.MODU, name)
      ) { modu, bookmark ->
         ModuWithBookmark(modu, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}