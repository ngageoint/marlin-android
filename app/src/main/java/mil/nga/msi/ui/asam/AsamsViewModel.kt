package mil.nga.msi.ui.asam

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.asam.AsamLocalDataSource
import javax.inject.Inject

@HiltViewModel
class AsamsViewModel @Inject constructor(
   private val localDataSource: AsamLocalDataSource
): ViewModel() {
   val asams = Pager(PagingConfig(pageSize = 20), null) {
      localDataSource.getAsamPages()
   }.flow
}