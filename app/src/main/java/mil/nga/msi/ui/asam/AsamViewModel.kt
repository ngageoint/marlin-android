package mil.nga.msi.ui.asam

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
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.AsamTileRepository
import mil.nga.msi.ui.map.overlay.AsamTileProvider
import javax.inject.Inject

@HiltViewModel
class AsamViewModel @Inject constructor(
   private val application: Application,
   private val repository: AsamRepository,
   private val dataSource: AsamLocalDataSource,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   private val referenceFlow = MutableSharedFlow<String>(replay = 1)
   fun setAsamReference(reference: String) {
      viewModelScope.launch {
         referenceFlow.emit(reference)
      }
   }

   val tileProvider = referenceFlow.map { reference ->
      val tileRepository = AsamTileRepository(reference, dataSource)
      AsamTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val asamWithBookmark = referenceFlow.flatMapLatest { reference ->
      combine(
         repository.observeAsam(reference),
         bookmarkRepository.observeBookmark(DataSource.ASAM, reference)
      ) { asam, bookmark ->
         AsamWithBookmark(asam, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}