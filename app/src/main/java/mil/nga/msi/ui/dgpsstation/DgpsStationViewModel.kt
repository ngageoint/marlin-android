package mil.nga.msi.ui.dgpsstation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DgpsStationViewModel @Inject constructor(
   private val repository: DgpsStationRepository,
   userPreferencesRepository: UserPreferencesRepository,
   @Named("dgpsStationTileProvider") val tileProvider: TileProvider
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getDgpsStation(volumeNumber: String, featureNumber: Int): LiveData<DgpsStation> {
      return repository.observeDgpsStation(volumeNumber, featureNumber).asLiveData()
   }
}