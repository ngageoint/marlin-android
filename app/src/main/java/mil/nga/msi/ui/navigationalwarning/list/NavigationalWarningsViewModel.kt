package mil.nga.msi.ui.navigationalwarning.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningsViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   suspend fun getNavigationalWarning(key: NavigationalWarningKey): NavigationalWarning? {
      return repository.getNavigationalWarning(key)
   }

   private val _navigationArea = MutableStateFlow<NavigationArea?>(null)
   val navigationArea: StateFlow<NavigationArea?> = _navigationArea
   fun setNavigationArea(navigationArea: NavigationArea) {
      _navigationArea.value = navigationArea
   }

   val navigationalWarningsByArea = navigationArea.flatMapLatest { navigationArea ->
      navigationArea?.let {
         if (navigationArea == NavigationArea.UNPARSED) {
            repository.observeUnparsedNavigationalWarnings()
         } else {
            repository.getNavigationalWarningsByArea(it)
         }
      } ?: emptyFlow()
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   fun getLastViewedWarning(): LiveData<NavigationalWarning?> {
      return navigationArea.filterNotNull().flatMapLatest { navigationArea ->
         if (navigationArea == NavigationArea.UNPARSED) {
            emptyFlow()
         } else {
            userPreferencesRepository.lastReadNavigationalWarnings.flatMapLatest { map ->
               val preferenceKey = map[navigationArea.code]!!
               val key = NavigationalWarningKey(preferenceKey.number.toInt(), preferenceKey.year, navigationArea)
               repository.observeNavigationalWarning(key)
            }
         }
      }.asLiveData()
   }

   fun setNavigationalWarningViewed(navigationArea: NavigationArea, item: NavigationalWarningListItem) {
      viewModelScope.launch {
         userPreferencesRepository.setLastReadNavigationalWarning(navigationArea, NavigationalWarningKey.fromNavigationWarning(item))
      }
   }
}