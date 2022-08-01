package mil.nga.msi.ui.light

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class LightViewModel @Inject constructor(
   private val repository: LightRepository,
   private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getLight(volumeNumber: String, featureNumber: String): LiveData<List<Light>> {
      return repository.observeLight(volumeNumber, featureNumber).asLiveData()
   }
}