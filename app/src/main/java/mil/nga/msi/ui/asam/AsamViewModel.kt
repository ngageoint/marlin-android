package mil.nga.msi.ui.asam

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AsamViewModel @Inject constructor(
   private val repository: AsamRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("asamTileProvider") val tileProvider: TileProvider

): ViewModel() {
   fun getAsam(id: String): LiveData<Asam> {
      return repository.observeAsam(id)
   }

   fun toggleBookmark(asam: Asam, notes: String? = null) {
      viewModelScope.launch {
         bookmarkRepository.setBookmark(asam, !asam.bookmarked, notes)
      }
   }
}