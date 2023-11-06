package mil.nga.msi.ui.radiobeacon

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
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RadioBeaconViewModel @Inject constructor(
   private val radioBeaconRepository: RadioBeaconRepository,
   private val bookmarkRepository: BookmarkRepository,
   @Named("radioBeaconTileProvider") val tileProvider: TileProvider
): ViewModel() {

   private val _keyFlow = MutableSharedFlow<RadioBeaconKey>(replay = 1)
   fun setRadioBeaconKey(key: RadioBeaconKey) {
      viewModelScope.launch {
         _keyFlow.emit(key)
      }
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val radioBeaconWithBookmark = _keyFlow.flatMapLatest { key ->
      combine(
         radioBeaconRepository.observeRadioBeacon(key),
         bookmarkRepository.observeBookmark(DataSource.RADIO_BEACON, key.id())
      ) { beacon, bookmark ->
         RadioBeaconWithBookmark(beacon, bookmark)
      }
   }.asLiveData()

   fun deleteBookmark(bookmark: Bookmark) {
      viewModelScope.launch {
         bookmarkRepository.delete(bookmark)
      }
   }

}