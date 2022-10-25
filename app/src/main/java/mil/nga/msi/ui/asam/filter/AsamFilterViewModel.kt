package mil.nga.msi.ui.asam.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class AsamFilterViewModel @Inject constructor(
   private val filterRepository: FilterRepository,
): ViewModel() {
   val filters = filterRepository.filters.transform { filters ->
      val asamFilters = filters[DataSource.ASAM] ?: emptyList()
      emit(asamFilters)
   }.asLiveData()

   fun setFilters(filters: List<AsamFilter>) {
      viewModelScope.launch {
         filterRepository.setFilter(DataSource.ASAM, filters)
      }
   }
}