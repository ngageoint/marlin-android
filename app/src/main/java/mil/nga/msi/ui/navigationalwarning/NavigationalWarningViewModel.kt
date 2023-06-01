package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.ui.map.MapShape
import javax.inject.Inject

data class NavigationalWarningState(
   val warning: NavigationalWarning,
   val annotations: List<MapShape>
)

@HiltViewModel
class NavigationalWarningViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository,
   userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getNavigationalWarning(key: NavigationalWarningKey): LiveData<NavigationalWarningState> {
       return repository.observeNavigationalWarning(key).mapNotNull { warning ->
         warning?.let {
            val annotations = warning.getFeatures().mapNotNull { feature ->
               MapShape.fromGeometry(feature, warning.id)
            }
            NavigationalWarningState(warning, annotations)
         }
       }
       .flowOn(Dispatchers.IO)
       .asLiveData()
   }
}