package mil.nga.msi.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.DataSourceRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

data class Tab(
   val dataSource: DataSource,
   val route: Route
)

@HiltViewModel
class NavigationViewModel @Inject constructor(
   dataSourceRepository: DataSourceRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val mapped = userPreferencesRepository.mapped.asLiveData()

   val tabs = userPreferencesRepository.tabs.mapNotNull { dataSources ->
      dataSources.mapNotNull { dataSource ->
         dataSource.tab?.let { tab -> Tab(dataSource, tab.route) }
      }
   }.asLiveData()

   val nonTabs = userPreferencesRepository.nonTabs.mapNotNull { dataSources ->
      dataSources.mapNotNull { dataSource ->
         dataSource.tab?.let { tab -> Tab(dataSource, tab.route) }
      }
   }.asLiveData()

   val fetching = dataSourceRepository.fetching

   fun setTabs(tabs: List<Tab>) {
      viewModelScope.launch {
         userPreferencesRepository.setTabs(tabs.map { it.dataSource })
      }
   }

   fun setNonTabs(tabs: List<Tab>) {
      viewModelScope.launch {
         userPreferencesRepository.setNonTabs(tabs.map { it.dataSource })
      }
   }
}