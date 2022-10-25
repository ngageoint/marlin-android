package mil.nga.msi.ui.asam.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.preferences.FilterRepository
import javax.inject.Inject

@HiltViewModel
class AsamsViewModel @Inject constructor(
   private val repository: AsamRepository,
   private val filterRepository: FilterRepository
): ViewModel() {
   suspend fun getAsam(reference: String): Asam? {
      return repository.getAsam(reference)
   }

   val asams: Flow<PagingData<AsamListItem>> = filterRepository.filters.flatMapLatest { entry ->
      val filters = entry[DataSource.ASAM] ?: emptyList()
      Pager(PagingConfig(pageSize = 20), null) {
         repository.observeAsamListItems(filters)
      }.flow
   }
}