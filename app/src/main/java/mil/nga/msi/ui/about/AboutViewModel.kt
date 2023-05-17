package mil.nga.msi.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val developer = userPreferencesRepository.developer().asLiveData()

   fun setDeveloperMode() {
      viewModelScope.launch {
         userPreferencesRepository.setDeveloperMode()
      }
   }

   fun setShowNoLocationNavigationWarnings(show: Boolean) {
      viewModelScope.launch {
         userPreferencesRepository.setShowNoLocationNavigationWarnings(show)
      }
   }
}