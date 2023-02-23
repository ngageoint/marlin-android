package mil.nga.msi.ui.noticetomariners.corrections

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.noticetomariners.ChartCorrection
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class NoticeToMarinersCorrectionsViewModel @Inject constructor(
   val locationPolicy: LocationPolicy,
   filterRepository: FilterRepository,
   private val repository: NoticeToMarinersRepository
): ViewModel() {
   private val _loading = MutableLiveData(true)
   val loading: LiveData<Boolean> = _loading

   private val _corrections = MutableLiveData<Map<String, List<ChartCorrection>>>()
   val corrections: LiveData<Map<String, List<ChartCorrection>>> = _corrections

   init {
      viewModelScope.launch(Dispatchers.IO) {
         val filters = filterRepository.filters.first()
         val ntmFilters = filters[DataSource.NOTICE_TO_MARINERS]
         val locationFilter = ntmFilters?.getOrNull(0)
         val noticeFilter = ntmFilters?.getOrNull(1)
         val corrections = repository.getNoticeToMarinersCorrections(locationFilter, noticeFilter)
         val group  = corrections
            .groupBy { it.chartNumber }
            .toSortedMap(compareBy { it.toInt() } )
            .mapValues { (_, notices) ->
               notices
                  .sortedWith(compareByDescending<ChartCorrection> { it.noticeYear }
                  .thenByDescending { it.noticeWeek })
            }
         _corrections.postValue(group)
         _loading.postValue(false)
      }
   }
}