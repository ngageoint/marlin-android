package mil.nga.msi.repository.bookmark

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.repository.dgpsstation.DgpsStationKey
import java.util.Date
import javax.inject.Inject

class BookmarkLocalDataSource @Inject constructor(
   private val asamDao: AsamDao,
   private val dgpsStationDao: DgpsStationDao,
   private val moduDao: ModuDao
) {

   fun observeBookmarks(): Flow<List<Bookmark>> {
      return combine(
         asamDao.observeBookmarkedAsams(),
         dgpsStationDao.observeBookmarkedDgpsStations(),
         moduDao.observeBookmarkedModus()
      ) { asams, dgpsStations, modus ->
         val bookmarks = asams + dgpsStations + modus
         bookmarks.sortedByDescending { it.bookmarkDate }
      }
   }

   suspend fun setBookmark(
      bookmark: BookmarkKey,
      bookmarked: Boolean,
      notes: String? = null
   ) {
      val date = if(bookmarked) Date() else null
      when (bookmark.dataSource) {
         DataSource.ASAM -> asamDao.setBookmark(bookmark.id, bookmarked, date, notes)
         DataSource.MODU -> moduDao.setBookmark(bookmark.id, bookmarked, date, notes)
         DataSource.DGPS_STATION -> {
            val key = DgpsStationKey.fromId(bookmark.id)
            dgpsStationDao.setBookmark(key.volumeNumber, key.featureNumber, bookmarked, date, notes)
         }
         else -> {}
      }
   }
}