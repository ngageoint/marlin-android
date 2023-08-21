package mil.nga.msi.ui.noticetomariners.detail

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import javax.inject.Inject

data class NoticeToMarinersPublication(
   val notice: NoticeToMariners,
   val uri: Uri? = null
)

data class NoticeToMarinersState(
   val noticeNumber: Int,
   val publications: List<NoticeToMarinersPublication>,
   val graphics: List<NoticeToMarinersGraphics>,
   val bookmark: Bookmark? = null
)

@HiltViewModel
class NoticeToMarinersDetailViewModel @Inject constructor(
   private val application: Application,
   private val noticeToMarinersRepository: NoticeToMarinersRepository,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {

   private val _loading = MutableLiveData(false)
   val loading: LiveData<Boolean> = _loading

   private val _noticeNumberFlow = MutableSharedFlow<Int>(replay = 1)
   fun setNoticeNumber(noticeNumber: Int) {
      viewModelScope.launch {
         _noticeNumberFlow.emit(noticeNumber)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val noticeToMariners = _noticeNumberFlow.flatMapLatest { noticeNumber ->
      _loading.value = true

      combine(
         noticeToMarinersRepository.observeNoticeToMariners(noticeNumber),
         bookmarkRepository.observeBookmark(DataSource.NOTICE_TO_MARINERS, noticeNumber.toString())
      ) { notices, _ ->
         notices.sortedWith(
            compareBy({
               it.isFullPublication == false
            },{
               it.sectionOrder
            },{
               application.contentResolver.getType(Uri.parse(it.filename))
            })
         )
      }.map { notices ->
         val bookmark = bookmarkRepository.getBookmark(DataSource.NOTICE_TO_MARINERS, noticeNumber.toString())

         val publications = notices.map {
            NoticeToMarinersPublication(
               notice = it,
               uri = noticeToMarinersPublicationUri(it.filename)
            )
         }

         val graphics = noticeToMarinersRepository.getNoticeToMarinersGraphics(noticeNumber)
         val state = NoticeToMarinersState(
            noticeNumber = noticeNumber,
            publications = publications,
            bookmark = bookmark,
            graphics = graphics
         )

         _loading.value = false

         state
      }
   }.asLiveData()

   suspend fun getNoticeToMarinersPublication(notice: NoticeToMariners): Uri? {
      return noticeToMarinersPublicationUri(notice.filename)
         ?: noticeToMarinersRepository.getNoticeToMarinersPublication(notice)
   }

   suspend fun deleteNoticeToMarinersPublication(notice: NoticeToMariners) {
      noticeToMarinersRepository.deleteNoticeToMarinersPublication(notice)
   }

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   private fun noticeToMarinersPublicationUri(filename: String): Uri? {
      val path = NoticeToMariners.externalFilesPath(application, filename)
      val file = path.toFile()

      return if(file.exists()) {
         FileProvider.getUriForFile(application, "${application.packageName}.fileprovider", file)
      } else {
         null
      }
   }
}