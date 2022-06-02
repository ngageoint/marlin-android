package mil.nga.msi.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamMapItem
import mil.nga.msi.datasource.modu.ModuMapItem
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
   asamRepository: AsamRepository,
   moduRepository: ModuRepository
): ViewModel() {
   val asams: LiveData<List<AsamMapItem>> = asamRepository.asamMapItems.asLiveData()
   val modus: LiveData<List<ModuMapItem>> = moduRepository.moduMapItems.asLiveData()
}