package mil.nga.msi.repository.bookmark

import javax.inject.Inject

class BookmarkRepository @Inject constructor(
   private val localDataSource: BookmarkLocalDataSource
) {
   fun observeBookmarks() = localDataSource.observeBookmarks()

   suspend fun setBookmark(
      item: Any,
      bookmarked: Boolean,
      notes: String? = null
   ) = localDataSource.setBookmark(item, bookmarked, notes)
}