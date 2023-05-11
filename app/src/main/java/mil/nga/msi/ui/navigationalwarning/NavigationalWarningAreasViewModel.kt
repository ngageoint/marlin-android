package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningAreasViewModel @Inject constructor(
   private val locationPolicy: LocationPolicy,
   val repository: NavigationalWarningRepository,
   val userPreferencesRepository: UserPreferencesRepository,
   @Named("naturalEarth_1_100") val naturalEarthTileProvider: TileProvider,
   @Named("navigationAreas") val navigationAreaTileProvider: TileProvider
): ViewModel() {

   val locationProvider = locationPolicy.bestLocationProvider

   fun setLocationEnabled(enabled: Boolean) {
      if (enabled) {
         locationPolicy.requestLocationUpdates()
      }
   }

   val warnings = repository.observeNavigationalWarnings()
      .mapLatest {
         it.map { warning ->
            NavigationalWarningState.fromWarning(warning)
         }
      }
      .flowOn(Dispatchers.IO)
      .asLiveData()

   val unparsedWarnings = userPreferencesRepository.developer().map { developer ->
      developer.showNonParsedNavigationWarnings
   }.flatMapLatest { show ->
      if (show) {
         repository.observeUnparsedNavigationalWarnings()
      } else emptyFlow()
   }.asLiveData()

   val navigationalWarningsByArea = userPreferencesRepository.lastReadNavigationalWarnings.flatMapLatest {
      repository.getNavigationalWarningsByNavigationArea(
         getNavigationalWarning(it[NavigationArea.HYDROARC.code]!!, NavigationArea.HYDROARC),
         getNavigationalWarning(it[NavigationArea.HYDROLANT.code]!!, NavigationArea.HYDROLANT),
         getNavigationalWarning(it[NavigationArea.HYDROPAC.code]!!, NavigationArea.HYDROPAC),
         getNavigationalWarning(it[NavigationArea.NAVAREA_IV.code]!!, NavigationArea.NAVAREA_IV),
         getNavigationalWarning(it[NavigationArea.NAVAREA_XII.code]!!, NavigationArea.NAVAREA_XII),
         getNavigationalWarning(it[NavigationArea.SPECIAL_WARNING.code]!!, NavigationArea.SPECIAL_WARNING),
      )
   }.asLiveData()

   private suspend fun getNavigationalWarning(preferenceKey: mil.nga.msi.type.NavigationalWarningKey, navigationArea: NavigationArea): Date {
      val key = NavigationalWarningKey(preferenceKey.number.toInt(), preferenceKey.year, navigationArea)
      return repository.getNavigationalWarning(key)?.issueDate ?: Date(0)
   }
}