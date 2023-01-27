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

//   private val queryParameters = MediatorLiveData<Pair<List<Filter>, Sort?>>().apply {
//      addSource(filterRepository.filters.asLiveData()) { entry ->
//         val filters = entry[DataSource.ASAM] ?: emptyList()
//         value = Pair(filters, value?.second)
//      }
//
//      addSource(sortRepository.sort.asLiveData()) { entry ->
//         val filters = value?.first ?: emptyList()
//         value = Pair(filters, entry[DataSource.ASAM])
//      }
//   }

//   val noticeToMariners = Pager(PagingConfig(pageSize = 20), null) {
//      noticeToMarinersRepository.observeNoticeToMarinersListItems()
//   }.flow.cachedIn(viewModelScope)

   private val _noticeToMariners = MutableLiveData<NoticeToMarinersState>()
   val noticeToMariners: LiveData<NoticeToMarinersState> = _noticeToMariners

   fun setNoticeNumber(noticeNumber: Int) {
      viewModelScope.launch {
         val notices = noticeToMarinersRepository
            .getNoticeToMariners(noticeNumber)
            .sortedWith(compareBy({ it.isFullPublication == false }, { it.sectionOrder }, { it.fileExtension }))

         val graphics = noticeToMarinersRepository.observeNoticeToMarinersGraphics(noticeNumber)
         _noticeToMariners.value = NoticeToMarinersState(notices, graphics)
      }
   }

//   val asamFilters = filterRepository.filters.map { entry ->
//      entry[DataSource.ASAM] ?: emptyList()
//   }.asLiveData()

//   suspend fun getNoticeToMariners(reference: String) = noticeToMarinersRepository.getAsam(reference)

//   private fun header(sort: SortParameter, item1: AsamListItem.AsamItem?, item2: AsamListItem.AsamItem?): AsamListItem.HeaderItem? {
//      return when (sort.parameter.type) {
//         FilterParameterType.DATE -> {
//            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
//            val date1 = item1?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
//            val date1String = date1?.format(formatter)
//            val date2 = item2?.asam?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
//            val date2String = date2?.format(formatter)
//
//            return if (date1String == null && date2String != null) {
//               AsamListItem.HeaderItem(date2String)
//            } else if (date1String != null && date2String != null && date1String != date2String) {
//               AsamListItem.HeaderItem(date2String)
//            } else null
//         }
//         else -> {
//            val item1String = parameterToName(sort.parameter.parameter, item1?.asam)
//            val item2String = parameterToName(sort.parameter.parameter, item2?.asam)
//
//            return if (item1String == null && item2String != null) {
//               AsamListItem.HeaderItem(item2String)
//            } else if (item1String != null && item2String != null && item1String != item2String) {
//               AsamListItem.HeaderItem(item2String)
//            } else null
//         }
//      }
//   }
}