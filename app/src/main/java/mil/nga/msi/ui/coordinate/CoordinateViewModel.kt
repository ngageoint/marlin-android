package mil.nga.msi.ui.coordinate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.preferences.MapRepository
import javax.inject.Inject

@HiltViewModel
class CoordinateViewModel @Inject constructor(
   mapRepository: MapRepository
): ViewModel() {
   val coordinateSystem = mapRepository.coordinateSystem.asLiveData()
}