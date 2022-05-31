package mil.nga.msi.ui.asam

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRepository
import javax.inject.Inject

@HiltViewModel
class AsamViewModel @Inject constructor(
   private val repository: AsamRepository
): ViewModel() {
   fun getAsam(id: String): LiveData<Asam> {
      return repository.observeAsam(id)
   }
}