package mil.nga.msi.ui.noticetomariners.detail

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersGraphic
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import javax.inject.Inject

@HiltViewModel
class NoticeToMarinersGraphicViewModel @Inject constructor(
   private val noticeToMarinersRepository: NoticeToMarinersRepository,
): ViewModel() {
   private val _downloading = MutableLiveData(false)
   val downloading: LiveData<Boolean> = _downloading

   suspend fun getNoticeToMarinersGraphic(graphic: NoticeToMarinersGraphic): Uri? {
      _downloading.value = true
      val uri = noticeToMarinersRepository.getNoticeToMarinersGraphic(graphic)
      _downloading.value = false

      return uri
   }
}