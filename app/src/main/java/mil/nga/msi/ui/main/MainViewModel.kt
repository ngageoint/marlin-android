package mil.nga.msi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.preferences.EmbarkRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.matomo.sdk.Tracker
import org.matomo.sdk.extra.TrackHelper
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
   embarkRepository: EmbarkRepository,
   userPreferencesRepository: UserPreferencesRepository,
   private val tracker: Tracker
): ViewModel() {

   val embark = embarkRepository.embark.asLiveData()
   val tabs = userPreferencesRepository.tabs.asLiveData()

   fun track(destination: NavDestination) {
      TrackHelper
         .track()
         .screen(destination.route?.substringBefore("?"))
         .with(tracker)
   }

}