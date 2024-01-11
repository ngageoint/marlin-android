package mil.nga.msi.ui.dgpsstation

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
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.map.DgpsStationTileRepository
import mil.nga.msi.ui.map.overlay.DgpsStationTileProvider
import javax.inject.Inject

@HiltViewModel
class DgpsStationViewModel @Inject constructor(
   private val application: Application,
   private val repository: DgpsStationRepository,
   private val dataSource: DgpsStationLocalDataSource,
   private val bookmarkRepository: BookmarkRepository
): ViewModel() {
   private val keyFlow = MutableSharedFlow<DgpsStationKey>(replay = 1)
   fun setDgpsStationKey(key: DgpsStationKey) {
      viewModelScope.launch {
         keyFlow.emit(key)
      }
   }

   val tileProvider = keyFlow.map { key ->
      val tileRepository = DgpsStationTileRepository(key, dataSource)
      DgpsStationTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val dgpsStationWithBookmark = keyFlow.flatMapLatest { key ->
      combine(
         repository.observeDgpsStation(key.volumeNumber, key.featureNumber),
         bookmarkRepository.observeBookmark(DataSource.DGPS_STATION, key.id())
      ) { dgpsStation, bookmark ->
         DgpsStationWithBookmark(dgpsStation, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }
}