package mil.nga.msi.ui.dgpsstation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DgpsStationViewModel @Inject constructor(
   private val repository: DgpsStationRepository,
   @Named("dgpsStationTileProvider") val tileProvider: TileProvider
): ViewModel() {
   fun getDgpsStation(volumeNumber: String, featureNumber: Float): LiveData<DgpsStation> {
      return repository.observeDgpsStation(volumeNumber, featureNumber).asLiveData()
   }
}