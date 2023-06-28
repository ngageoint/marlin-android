package mil.nga.msi.ui.asam

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
import mil.nga.msi.datasource.asam.AsamWithBookmark
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AsamViewModel @Inject constructor(
   private val asamRepository: AsamRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("asamTileProvider") val tileProvider: TileProvider

): ViewModel() {
   private val _referenceFlow = MutableSharedFlow<String>()
   fun setAsamReference(reference: String) {
      viewModelScope.launch {
         _referenceFlow.emit(reference)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val asamWithBookmark = _referenceFlow.flatMapLatest { reference ->
      combine(
         asamRepository.observeAsam(reference),
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