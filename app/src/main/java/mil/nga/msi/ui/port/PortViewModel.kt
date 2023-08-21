package mil.nga.msi.ui.port

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.TileProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.port.PortRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PortViewModel @Inject constructor(
   private val portRepository: PortRepository,
   private val bookmarkRepository: BookmarkRepository,
   locationPolicy: LocationPolicy,
   @Named("portTileProvider") val tileProvider: TileProvider
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   private val _portNumberFlow = MutableSharedFlow<Int>(replay = 1)
   fun setPortNumber(name: Int) {
      viewModelScope.launch {
         _portNumberFlow.emit(name)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val portWithBookmark = _portNumberFlow.flatMapLatest { portNumber ->
      combine(
         portRepository.observePort(portNumber),
         bookmarkRepository.observeBookmark(DataSource.PORT, portNumber.toString()),
      ) { port, bookmark ->
         PortWithBookmark(port, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}