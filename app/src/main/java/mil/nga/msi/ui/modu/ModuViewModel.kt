package mil.nga.msi.ui.modu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.bookmark.BookmarkKey
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
   fun getModu(name: String): LiveData<Modu> {
      return moduRepository.observeModu(name)
   }

   fun removeBookmark(modu: Modu) {
      viewModelScope.launch {
         bookmarkRepository.setBookmark(BookmarkKey.fromModu(modu), false)
      }
   }
}