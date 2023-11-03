package mil.nga.msi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapNotNull
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.preferences.EmbarkRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.AnnotationProvider
import mil.nga.msi.ui.navigation.Route
import org.matomo.sdk.Tracker
import org.matomo.sdk.extra.TrackHelper
import javax.inject.Inject

data class Tab(
   val dataSource: DataSource,
   val route: Route
)

@HiltViewModel
class MainViewModel @Inject constructor(
   embarkRepository: EmbarkRepository,
   userPreferencesRepository: UserPreferencesRepository,
   val annotationProvider: AnnotationProvider,
   private val tracker: Tracker
): ViewModel() {

   val embark = embarkRepository.embark.asLiveData()
   val tabs = userPreferencesRepository.tabs.mapNotNull { dataSources ->
      dataSources.mapNotNull { dataSource -> dataSource.tab?.let { Tab(dataSource, it.route) }  }
   }.asLiveData()

   fun track(destination: NavDestination) {
      TrackHelper
         .track()
         .screen(destination.route?.substringBefore("?"))
         .with(tracker)
   }
}