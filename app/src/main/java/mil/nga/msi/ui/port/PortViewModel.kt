package mil.nga.msi.ui.port

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.port.PortWithBookmark
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.PortTileRepository
import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.ui.map.overlay.PortTileProvider
import javax.inject.Inject

@HiltViewModel
class PortViewModel @Inject constructor(
   private val application: Application,
   private val dataSource: PortLocalDataSource,
   private val portRepository: PortRepository,
   private val bookmarkRepository: BookmarkRepository,
   locationPolicy: LocationPolicy
): ViewModel() {
   val locationProvider = locationPolicy.bestLocationProvider

   private val portNumberFlow = MutableSharedFlow<Int>(replay = 1)
   fun setPortNumber(name: Int) {
      viewModelScope.launch {
         portNumberFlow.emit(name)
      }
   }

   val tileProvider = portNumberFlow.map { name ->
      val tileRepository = PortTileRepository(name, dataSource)
      PortTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val portWithBookmark = portNumberFlow.flatMapLatest { portNumber ->
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