package mil.nga.msi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
   userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

   val tabs = userPreferencesRepository.tabs.asLiveData()

}