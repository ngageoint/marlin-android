package mil.nga.msi.repository.bookmark

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.bookmark.Bookmark
import java.util.Date
import javax.inject.Inject

class BookmarkLocalDataSource @Inject constructor(
   private val asamDao: AsamDao
) {

   fun observeBookmarks(): Flow<List<Bookmark>> {
      val asams = asamDao.observeBookmarkedAsams()
      return merge(asams)
   }

   suspend fun setBookmark(
      bookmark: BookmarkKey,
      bookmarked: Boolean,
      notes: String? = null
   ) {
      val date = if(bookmarked) Date() else null
      when (bookmark.dataSource) {
         DataSource.ASAM -> asamDao.setBookmark(bookmark.id, bookmarked, date, notes)
         else -> {}
      }
   }
}