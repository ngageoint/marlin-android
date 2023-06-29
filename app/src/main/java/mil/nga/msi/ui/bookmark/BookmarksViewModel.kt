package mil.nga.msi.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.bookmark.BookmarkRepository
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.light.LightKey
import mil.nga.msi.repository.light.LightRepository
import mil.nga.msi.repository.modu.ModuRepository
import javax.inject.Inject

data class ItemWithBookmark(
   val item: Any,
   val bookmark: Bookmark
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
   private val repository: BookmarkRepository,
   private val asamRepository: AsamRepository,
   private val dgpsStationRepository: DgpsStationRepository,
   private val lightRepository: LightRepository,
   private val moduRepository: ModuRepository,
): ViewModel() {
   val bookmarks = repository.observeBookmarks().map { bookmarks ->
      bookmarks.mapNotNull { bookmark ->
         when(bookmark.dataSource) {
            DataSource.ASAM -> {
               asamRepository.getAsam(bookmark.id)?.let { asam ->
                  ItemWithBookmark(asam, bookmark)
               }
            }
            DataSource.MODU -> {
               moduRepository.getModu(bookmark.id)?.let { modu ->
                  ItemWithBookmark(modu, bookmark)
               }
            }
            DataSource.LIGHT -> {
               val key = LightKey.fromId(bookmark.id)
               lightRepository.getLight(key.volumeNumber, key.featureNumber, key.characteristicNumber)?.let { light ->
                  ItemWithBookmark(light, bookmark)
               }
            }
            DataSource.DGPS_STATION -> {
               val key = DgpsStationKey.fromId(bookmark.id)
               dgpsStationRepository.getDgpsStation(key.volumeNumber, key.featureNumber)?.let { dgpsStation ->
                  ItemWithBookmark(dgpsStation, bookmark)
               }
            }
            else -> null
         }
      }
   }.asLiveData()

   fun deleteBookmark(key: BookmarkKey) {
      viewModelScope.launch {
         val bookmark = Bookmark(key.id, key.dataSource)
         repository.delete(bookmark)
      }
   }
}