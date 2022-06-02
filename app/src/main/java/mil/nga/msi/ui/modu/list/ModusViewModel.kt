package mil.nga.msi.ui.modu.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject

@HiltViewModel
class ModusViewModel @Inject constructor(
   private val repository: ModuRepository
): ViewModel() {
   val modus = Pager(PagingConfig(pageSize = 20), null) {
      repository.moduListItems
   }.flow
}