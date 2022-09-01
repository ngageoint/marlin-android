package mil.nga.msi.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

   val mapped = userPreferencesRepository.mapped.asLiveData()
   val tabs = userPreferencesRepository.tabs.asLiveData()
   val nonTabs = userPreferencesRepository.nonTabs.asLiveData()

   suspend fun toggleOnMap(dataSource: DataSource) {
     userPreferencesRepository.setMapped(dataSource)
   }

   fun setTabs(tabs: List<DataSource>) {
      viewModelScope.launch {
         userPreferencesRepository.setTabs(tabs)
      }
   }

   fun setNonTabs(tabs: List<DataSource>) {
      viewModelScope.launch {
         userPreferencesRepository.setNonTabs(tabs)
      }
   }
}