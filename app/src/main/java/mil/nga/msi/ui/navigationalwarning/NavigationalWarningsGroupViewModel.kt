package mil.nga.msi.ui.navigationalwarning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.ui.map.overlay.GeoPackageTileProvider
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class NavigationalWarningsGroupViewModel @Inject constructor(
   repository: NavigationalWarningRepository,
   @Named("lowResolution") val naturalEarthTileProvider: GeoPackageTileProvider
): ViewModel() {
   val navigationalWarningsByArea = repository.getNavigationalWarningsGroupByArea().asLiveData()
}