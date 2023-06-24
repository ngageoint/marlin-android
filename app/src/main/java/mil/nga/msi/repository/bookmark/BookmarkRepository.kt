package mil.nga.msi.repository.bookmark

import javax.inject.Inject

class BookmarkRepository @Inject constructor(
   private val localDataSource: BookmarkLocalDataSource
) {
   fun observeBookmarks() = localDataSource.observeBookmarks()

   suspend fun setBookmark(
      bookmark: BookmarkKey,
      bookmarked: Boolean,
      notes: String? = null
   ) = localDataSource.setBookmark(bookmark, bookmarked, notes)
}