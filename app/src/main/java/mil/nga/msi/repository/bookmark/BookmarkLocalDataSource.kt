package mil.nga.msi.repository.bookmark

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import mil.nga.msi.datasource.asam.Asam
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
      item: Any,
      bookmarked: Boolean,
      notes: String? = null
   ) {
      val date = if(bookmarked) Date() else null
      when (item) {
         is Asam -> asamDao.setBookmarked(item.reference, bookmarked, date, notes)
         else -> {}
      }
   }
}