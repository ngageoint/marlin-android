package mil.nga.msi.ui.modu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject

@HiltViewModel
class ModuViewModel @Inject constructor(
   private val repository: ModuRepository
): ViewModel() {
   fun getModu(name: String): LiveData<Modu> {
      return repository.observeModu(name)
   }
}