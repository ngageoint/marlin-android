package mil.nga.msi.ui.dgpsstation

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
import mil.nga.msi.datasource.dgpsstation.DgpsStationWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DgpsStationViewModel @Inject constructor(
   private val repository: DgpsStationRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("dgpsStationTileProvider") val tileProvider: TileProvider
): ViewModel() {
   private val _dgpsStationKeyFlow = MutableSharedFlow<DgpsStationKey>(replay = 1)
   fun setDgpsStationKey(key: DgpsStationKey) {
      viewModelScope.launch {
         _dgpsStationKeyFlow.emit(key)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val dgpsStationWithBookmark = _dgpsStationKeyFlow.flatMapLatest { key ->
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