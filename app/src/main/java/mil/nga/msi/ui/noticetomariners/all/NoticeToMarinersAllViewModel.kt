package mil.nga.msi.ui.noticetomariners.all

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import javax.inject.Inject

@HiltViewModel
class NoticeToMarinersAllViewModel @Inject constructor(
   noticeToMarinersRepository: NoticeToMarinersRepository,
): ViewModel() {
   val noticeToMariners = noticeToMarinersRepository
      .observeNoticeToMarinersListItems()
      .map { notices ->
         notices.groupBy { it.noticeNumber }.keys.sortedDescending()
      }.asLiveData()
}