package mil.nga.msi.ui.asam.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.asam.AsamRepository
import javax.inject.Inject

@HiltViewModel
class AsamsViewModel @Inject constructor(
   private val repository: AsamRepository
): ViewModel() {
   suspend fun getAsam(reference: String): Asam? {
      return repository.getAsam(reference)
   }

   val asams = Pager(PagingConfig(pageSize = 20), null) {
      repository.asamListItems
   }.flow
}