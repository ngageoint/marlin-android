package mil.nga.msi.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.bookmark.BookmarkRepository
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {

   fun saveBookmark(key: BookmarkKey, notes: String? = null) {
      viewModelScope.launch {
         val bookmark = Bookmark(id = key.id, dataSource = key.dataSource, notes = notes)
         bookmarkRepository.insert(bookmark)
      }
   }
}