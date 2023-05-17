package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.mapNotNull
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningKey
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class NavigationalWarningViewModel @Inject constructor(
   private val repository: NavigationalWarningRepository,
   userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getNavigationalWarning(key: NavigationalWarningKey): LiveData<NavigationalWarningState?> {
       return repository.observeNavigationalWarning(key).mapNotNull { warning ->
         warning?.let { NavigationalWarningState.fromWarning(it) }
       }.asLiveData()
   }
}