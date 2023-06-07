package mil.nga.msi.ui.radiobeacon

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RadioBeaconViewModel @Inject constructor(
   private val repository: RadioBeaconRepository,
   @Named("radioBeaconTileProvider") val tileProvider: TileProvider
): ViewModel() {
   fun getRadioBeacon(volumeNumber: String, featureNumber: String): LiveData<RadioBeacon> {
      return repository.observeRadioBeacon(volumeNumber, featureNumber).asLiveData()
   }
}