package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import javax.inject.Inject

@HiltViewModel
class NavigationalWarningViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository
): ViewModel() {
   fun getNavigationalWarning(key: NavigationalWarningKey): LiveData<NavigationalWarning> {
      return repository.observeNavigationalWarning(key)
   }
}