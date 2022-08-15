package mil.nga.msi.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.DataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.asam.AsamRoute
import mil.nga.msi.ui.light.LightRoute
import mil.nga.msi.ui.modu.ModuRoute
import mil.nga.msi.ui.navigationalwarning.NavigationWarningRoute
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
   private val repository: ModuRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

   val mapped = userPreferencesRepository.mapped.asLiveData()
   val tabs = userPreferencesRepository.tabs.asLiveData()

   suspend fun toggleOnMap(dataSource: DataSource) {
     userPreferencesRepository.setMapped(dataSource)
   }
}