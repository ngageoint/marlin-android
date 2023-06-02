package mil.nga.msi.ui.noticetomariners.query

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.filter.NoticeToMarinersFilter
import mil.nga.msi.filter.ComparatorType
import mil.nga.msi.filter.Filter
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

data class NoticeToMarinersLocationFilter(
   val comparator: ComparatorType,
   val location: LatLng? = null,
   val distance: Float? = null,
)

@HiltViewModel
class NoticeToMarinersQueryViewModel @Inject constructor(
   val locationPolicy: LocationPolicy,
   private val repository: FilterRepository
): ViewModel() {

   val locationParameter = NoticeToMarinersFilter.parameters.first()
   val locationFilter = repository.filters.transform { filters ->
      emit(filters[DataSource.NOTICE_TO_MARINERS]?.getOrNull(0))
   }.asLiveData()

   val noticeParameter = NoticeToMarinersFilter.parameters.last()
   val noticeFilter = repository.filters.transform { filters ->
      emit(filters[DataSource.NOTICE_TO_MARINERS]?.getOrNull(1))
   }.asLiveData()

   fun addLocationFilter(filter: Filter) {
      viewModelScope.launch {
         repository.setFilter(DataSource.NOTICE_TO_MARINERS, listOf(filter))
      }
   }

   fun removeLocationFilter() {
      viewModelScope.launch {
         repository.setFilter(DataSource.NOTICE_TO_MARINERS, listOf())
      }
   }

   fun addNoticeFilter(filter: Filter) {
      viewModelScope.launch {
         repository.filters.first()[DataSource.NOTICE_TO_MARINERS]?.getOrNull(0)?.let {
            repository.setFilter(DataSource.NOTICE_TO_MARINERS, listOf(it, filter))
         }
      }
   }

   fun removeNoticeFilter() {
      viewModelScope.launch {
         repository.filters.first()[DataSource.NOTICE_TO_MARINERS]?.getOrNull(0)?.let {
            repository.setFilter(DataSource.NOTICE_TO_MARINERS, listOf(it))
         }
      }
   }
}