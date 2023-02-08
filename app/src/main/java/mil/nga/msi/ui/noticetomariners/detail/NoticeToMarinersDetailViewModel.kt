package mil.nga.msi.ui.noticetomariners.detail

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

data class NoticeToMarinersPublication(
   val notice: NoticeToMariners,
   val uri: Uri? = null
)

data class NoticeToMarinersState(
   val publication: List<NoticeToMarinersPublication>,
   val graphics: List<NoticeToMarinersGraphics>
)

@HiltViewModel
class NoticeToMarinersDetailViewModel @Inject constructor(
   private val application: Application,
   private val noticeToMarinersRepository: NoticeToMarinersRepository,
): ViewModel() {

   private val _loading = MutableLiveData(false)
   val loading: LiveData<Boolean> = _loading

   private val _noticeToMariners = MutableLiveData<NoticeToMarinersState>()
   val noticeToMariners: LiveData<NoticeToMarinersState> = _noticeToMariners

   fun setNoticeNumber(noticeNumber: Int) {
      viewModelScope.launch {
         _loading.value = true

         val notices = noticeToMarinersRepository
            .getNoticeToMariners(noticeNumber)
            .sortedWith(compareBy({
                  it.isFullPublication == false
               },{
                  it.sectionOrder
               },{
                  application.contentResolver.getType(Uri.parse(it.filename))
               })
            )

         val publications = notices.map {
            NoticeToMarinersPublication(it, noticeToMarinersPublicationUri(it.filename))
         }
         val graphics = noticeToMarinersRepository.observeNoticeToMarinersGraphics(noticeNumber)
         _noticeToMariners.value = NoticeToMarinersState(publications, graphics)

         _loading.value = false
      }
   }

   suspend fun getNoticeToMarinersPublication(notice: NoticeToMariners): Uri {
      return noticeToMarinersPublicationUri(notice.filename)
         ?: noticeToMarinersRepository.getNoticeToMarinersPublication(notice)
   }

   suspend fun deleteNoticeToMarinersPublication(notice: NoticeToMariners) {
      noticeToMarinersRepository.deleteNoticeToMarinersPublication(notice)
   }

   private fun noticeToMarinersPublicationUri(filename: String): Uri? {
      val directory =  Paths.get(application.filesDir.absolutePath, "notice_to_mariners", "publications")
      val file = File(directory.toFile().absolutePath, filename)

      return if(file.exists()) {
         FileProvider.getUriForFile(application, "${application.packageName}.fileprovider", file)
      } else {
         null
      }
   }
}