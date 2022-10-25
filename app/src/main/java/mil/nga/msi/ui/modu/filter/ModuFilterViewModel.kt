package mil.nga.msi.ui.modu.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.filter.Filter
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class ModuFilterViewModel @Inject constructor(
   private val filterRepository: FilterRepository,
): ViewModel() {
   val filters = filterRepository.filters.transform { filters ->
      val asamFilters = filters[DataSource.MODU] ?: emptyList()
      emit(asamFilters)
   }.asLiveData()

   fun setFilters(filters: List<Filter>) {
      viewModelScope.launch {
         filterRepository.setFilter(DataSource.MODU, filters)
      }
   }
}