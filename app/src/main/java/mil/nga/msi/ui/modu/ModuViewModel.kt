package mil.nga.msi.ui.modu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.modu.ModuRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ModuViewModel @Inject constructor(
   private val repository: ModuRepository,
   userPreferencesRepository: UserPreferencesRepository,
   @Named("moduTileProvider") val tileProvider: TileProvider
): ViewModel() {
   val baseMap = userPreferencesRepository.baseMapType.asLiveData()

   fun getModu(name: String): LiveData<Modu> {
      return repository.observeModu(name)
   }
}