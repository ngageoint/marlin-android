package mil.nga.msi.ui.navigationalwarning.list

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import javax.inject.Inject

@HiltViewModel
class NavigationalWarningsViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository
): ViewModel() {
   suspend fun getNavigationalWarning(number: Int): NavigationalWarning? {
      return repository.getNavigationalWarning(number)
   }

   val navigationalWarnings = Pager(PagingConfig(pageSize = 20), null) {
      repository.getNavigationalWarningListItems()
   }.flow
}