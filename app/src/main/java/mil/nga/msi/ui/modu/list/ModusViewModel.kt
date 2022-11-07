package mil.nga.msi.ui.modu.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.ModuListItem
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class ModusViewModel @Inject constructor(
   private val repository: ModuRepository,
   filterRepository: FilterRepository
): ViewModel() {
   val modus: Flow<PagingData<ModuListItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.MODU] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observeModuListItems(filters)
      }.flow
   }

   val moduFilters = filterRepository.filters.map { entry ->
      entry[DataSource.MODU] ?: emptyList()
   }.asLiveData()

   suspend fun getModu(name: String) = repository.getModu(name)
}