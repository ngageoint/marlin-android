package mil.nga.msi.ui.port

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.port.PortRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PortViewModel @Inject constructor(
   private val repository: PortRepository,
   locationPolicy: LocationPolicy,
   @Named("portTileProvider") val tileProvider: TileProvider
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   fun getPort(portNumber: Int): LiveData<Port> {
      return repository.observePort(portNumber).asLiveData()
   }
}