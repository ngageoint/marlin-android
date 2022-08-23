package mil.nga.msi.ui.light

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LightViewModel @Inject constructor(
   private val repository: LightRepository,
   userPreferencesRepository: UserPreferencesRepository,
   @Named("lightTileProvider") val tileProvider: TileProvider
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getLight(volumeNumber: String, featureNumber: String): LiveData<List<Light>> {
      return repository.observeLight(volumeNumber, featureNumber).asLiveData()
   }
}