package mil.nga.msi.repository.bookmark

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.modu.ModuDao
import java.util.Date
import javax.inject.Inject

class BookmarkLocalDataSource @Inject constructor(
   private val asamDao: AsamDao,
   private val moduDao: ModuDao
) {

   fun observeBookmarks(): Flow<List<Bookmark>> {
      return combine(
         asamDao.observeBookmarkedAsams(),
         moduDao.observeBookmarkedModus()
      ) { asams, modus ->
         val bookmarks = asams + modus
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
         else -> {}
      }
   }
}