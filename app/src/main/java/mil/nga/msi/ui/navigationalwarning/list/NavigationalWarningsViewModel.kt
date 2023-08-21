package mil.nga.msi.ui.navigationalwarning.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItem
import mil.nga.msi.datasource.navigationwarning.NavigationalWarningListItemWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningsViewModel @Inject constructor(
   private val navigationalWarningRepository: NavigationalWarningRepository,
   private val bookmarkRepository: BookmarkRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   suspend fun getNavigationalWarning(key: NavigationalWarningKey): NavigationalWarning? {
      return navigationalWarningRepository.getNavigationalWarning(key)
   }

   private val _navigationArea = MutableStateFlow<NavigationArea?>(null)
   val navigationArea: StateFlow<NavigationArea?> = _navigationArea
   fun setNavigationArea(navigationArea: NavigationArea) {
      _navigationArea.value = navigationArea
   }

   val navigationalWarningsByArea = combine(
      _navigationArea,
      bookmarkRepository.observeBookmarks(DataSource.NAVIGATION_WARNING)
   ) { navigationArea, _ ->
      navigationArea
   }.flatMapLatest { navigationArea ->
      navigationArea?.let {
         val flow = if (navigationArea == NavigationArea.UNPARSED) {
            navigationalWarningRepository.observeUnparsedNavigationalWarnings()
         } else {
            navigationalWarningRepository.getNavigationalWarningsByArea(it)
         }

         flow.map { warnings ->
            warnings.map { warning ->
               val bookmark = bookmarkRepository.getBookmark(DataSource.NAVIGATION_WARNING, warning.id)
               NavigationalWarningListItemWithBookmark(warning, bookmark)
            }
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
               navigationalWarningRepository.observeNavigationalWarning(key)
            }
         }
      }.asLiveData()
   }

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

   fun setNavigationalWarningViewed(navigationArea: NavigationArea, item: NavigationalWarningListItem) {
      viewModelScope.launch {
         userPreferencesRepository.setLastReadNavigationalWarning(navigationArea, NavigationalWarningKey.fromNavigationWarning(item))
      }
   }
}