package mil.nga.msi.ui.embark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.EmbarkRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository.Companion.MAX_TABS
import javax.inject.Inject

@HiltViewModel
class EmbarkViewModel @Inject constructor(
   private val embarkRepository: EmbarkRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

   val embark = embarkRepository.embark.asLiveData()
   val disclaimer = embarkRepository.disclaimer.asLiveData()
   val location = embarkRepository.location.asLiveData()
   val notification = embarkRepository.notification.asLiveData()
   val tabs = embarkRepository.tabs.asLiveData()
   val selectedTabs = userPreferencesRepository.tabs.asLiveData()
   val map = embarkRepository.map.asLiveData()
   val selectedMap = userPreferencesRepository.mapped.asLiveData()

   fun setEmbark() {
      viewModelScope.launch {
         embarkRepository.setEmbark()
      }
   }

   fun setDisclaimer() {
      viewModelScope.launch {
         embarkRepository.setDisclaimer()
      }
   }

   fun setLocation() {
      viewModelScope.launch {
         embarkRepository.setLocation()
      }
   }

   fun setNotification() {
      viewModelScope.launch {
         embarkRepository.setNotification()
      }
   }

   fun setTabs() {
      viewModelScope.launch {
         embarkRepository.setTabs()
      }
   }

   fun toggleTab(dataSource: DataSource) {
      viewModelScope.launch {
         val selected = userPreferencesRepository.tabs.first().toMutableList()
         val nonSelected = userPreferencesRepository.nonTabs.first().toMutableList()

         if (selected.contains(dataSource)) {
            selected.remove(dataSource)
            nonSelected.add(dataSource)
         } else if (nonSelected.contains(dataSource)) {
            if (selected.size >= MAX_TABS) {
               selected.removeLastOrNull()?.let { removed ->
                  nonSelected.add(removed)
               }
            }

            selected.add(dataSource)
            nonSelected.remove(dataSource)
         }

         userPreferencesRepository.setTabs(selected)
         userPreferencesRepository.setNonTabs(nonSelected)
      }
   }

   fun setMap() {
      viewModelScope.launch {
         embarkRepository.setMap()
      }
   }

   fun toggleMap(dataSource: DataSource) {
      viewModelScope.launch {
         userPreferencesRepository.setMapped(dataSource)
      }
   }
}