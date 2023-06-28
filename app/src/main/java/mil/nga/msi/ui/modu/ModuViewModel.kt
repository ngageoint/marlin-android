package mil.nga.msi.ui.modu

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
import mil.nga.msi.datasource.modu.ModuWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ModuViewModel @Inject constructor(
   private val moduRepository: ModuRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("moduTileProvider") val tileProvider: TileProvider
): ViewModel() {
   private val _nameFlow = MutableSharedFlow<String>()
   fun setName(name: String) {
      viewModelScope.launch {
         _nameFlow.emit(name)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val moduWithBookmark = _nameFlow.flatMapLatest { name ->
      combine(
         moduRepository.observeModu(name),
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