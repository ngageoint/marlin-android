package mil.nga.msi.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkRepository
import javax.inject.Inject

sealed class BookmarkAction {
   class AsamBookmark(val asam: Asam) : BookmarkAction()
}

@HiltViewModel
class BookmarksViewModel @Inject constructor(
   bookmarkRepository: BookmarkRepository,
   private val repository: BookmarkRepository,
): ViewModel() {
   val bookmarks = bookmarkRepository.observeBookmarks().asLiveData()

   fun toggleBookmark(action: BookmarkAction) {
      viewModelScope.launch {
         when (action) {
            is BookmarkAction.AsamBookmark -> {
               repository.setBookmark(action.asam, !action.asam.bookmarked)
            }
         }
      }
   }
}