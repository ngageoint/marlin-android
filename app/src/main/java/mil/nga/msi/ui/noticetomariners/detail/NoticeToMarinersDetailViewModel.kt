package mil.nga.msi.ui.noticetomariners.detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
 import mil.nga.msi.datasource.noticetomariners.NoticeToMariners
import mil.nga.msi.datasource.noticetomariners.NoticeToMarinersGraphics
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import javax.inject.Inject

data class NoticeToMarinersState(
   val notices: List<NoticeToMariners>,
   val graphics: List<NoticeToMarinersGraphics>
)

@HiltViewModel
class NoticeToMarinersDetailViewModel @Inject constructor(
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
            .sortedWith(compareBy({ it.isFullPublication == false }, { it.sectionOrder }, { it.fileExtension }))

         val graphics = noticeToMarinersRepository.observeNoticeToMarinersGraphics(noticeNumber)
         _noticeToMariners.value = NoticeToMarinersState(notices, graphics)

         _loading.value = false
      }
   }
}