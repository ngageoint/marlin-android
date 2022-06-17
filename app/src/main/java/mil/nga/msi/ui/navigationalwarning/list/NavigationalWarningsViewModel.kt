package mil.nga.msi.ui.navigationalwarning.list

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import javax.inject.Inject

@HiltViewModel
class NavigationalWarningsViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository
): ViewModel() {
   suspend fun getNavigationalWarning(key: NavigationalWarningKey): NavigationalWarning? {
      return repository.getNavigationalWarning(key)
   }

   private val _navigationArea = MutableStateFlow<NavigationArea?>(null)
   val navigationArea: StateFlow<NavigationArea?> = _navigationArea
   fun setNavigationArea(navigationArea: NavigationArea) {
      _navigationArea.value = navigationArea
   }

   val navigationalWarningsByArea = navigationArea.flatMapLatest {
      Pager(PagingConfig(pageSize = 20), null) {
         repository.getNavigationalWarningsByArea(it)
      }.flow
   }
}