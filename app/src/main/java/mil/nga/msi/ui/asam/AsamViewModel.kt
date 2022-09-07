package mil.nga.msi.ui.asam

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AsamViewModel @Inject constructor(
   private val repository: AsamRepository,
   userPreferencesRepository: UserPreferencesRepository,
   @Named("asamTileProvider") val tileProvider: TileProvider

): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getAsam(id: String): LiveData<Asam> {
      return repository.observeAsam(id)
   }
}