package mil.nga.msi.ui.radiobeacon

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
import mil.nga.msi.datasource.radiobeacon.RadioBeaconWithBookmark
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.map.RadioBeaconTileRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconKey
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import mil.nga.msi.ui.map.overlay.RadioBeaconTileProvider
import javax.inject.Inject

@HiltViewModel
class RadioBeaconViewModel @Inject constructor(
   private val application: Application,
   private val dataSource: RadioBeaconLocalDataSource,
   private val radioBeaconRepository: RadioBeaconRepository,
   private val bookmarkRepository: BookmarkRepository,
): ViewModel() {

   private val keyFlow = MutableSharedFlow<RadioBeaconKey>(replay = 1)
   fun setRadioBeaconKey(key: RadioBeaconKey) {
      viewModelScope.launch {
         keyFlow.emit(key)
      }
   }

   val tileProvider = keyFlow.map { key ->
      val tileRepository = RadioBeaconTileRepository(key, dataSource)
      RadioBeaconTileProvider(application, tileRepository)
   }.asLiveData()

   @OptIn(ExperimentalCoroutinesApi::class)
   val radioBeaconWithBookmark = keyFlow.flatMapLatest { key ->
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