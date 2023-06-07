package mil.nga.msi.ui.light

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LightViewModel @Inject constructor(
   private val repository: LightRepository,
   @Named("lightTileProvider") val tileProvider: TileProvider
): ViewModel() {
   fun getLight(volumeNumber: String, featureNumber: String): LiveData<List<Light>> {
      return repository.observeLight(volumeNumber, featureNumber).asLiveData()
   }
}