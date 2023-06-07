package mil.nga.msi.ui.modu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ModuViewModel @Inject constructor(
   private val repository: ModuRepository,
   @Named("moduTileProvider") val tileProvider: TileProvider
): ViewModel() {
   fun getModu(name: String): LiveData<Modu> {
      return repository.observeModu(name)
   }
}