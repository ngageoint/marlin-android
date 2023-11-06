package mil.nga.msi.ui.noticetomariners.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import javax.inject.Inject

data class NoticeToMarinersWithBookmark(
   val noticeNumber: Int,
   val bookmark: Bookmark?
)

@HiltViewModel
class NoticeToMarinersAllViewModel @Inject constructor(
   noticeToMarinersRepository: NoticeToMarinersRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   val notices = combine(
      noticeToMarinersRepository.observeNoticeToMarinersListItems(),
      bookmarkRepository.observeBookmarks(DataSource.NOTICE_TO_MARINERS)
   ) { notices, _ ->
      notices
   }.map { notices ->
      notices.map {
         val bookmark = bookmarkRepository.getBookmark(DataSource.NOTICE_TO_MARINERS, it.noticeNumber.toString())
         NoticeToMarinersWithBookmark(it.noticeNumber, bookmark)
      }.toSet().groupBy {
         it.noticeNumber.toString().take(4)
      }
   }
   .asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}