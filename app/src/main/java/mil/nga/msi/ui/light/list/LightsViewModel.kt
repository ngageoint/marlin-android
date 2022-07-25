package mil.nga.msi.ui.light.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.repository.light.LightRepository
import javax.inject.Inject

@HiltViewModel
class LightsViewModel @Inject constructor(
   private val repository: LightRepository
): ViewModel() {
   suspend fun getLight(
      volumeNumber: String,
      featureNumber: String,
      characteristicNumber: Int
   ): Light? {
      return repository.getLight(volumeNumber, featureNumber, characteristicNumber)
   }

   val lights = repository.getLightListItems()
}