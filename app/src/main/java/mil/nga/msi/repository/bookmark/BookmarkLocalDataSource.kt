package mil.nga.msi.repository.bookmark

import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import mil.nga.msi.datasource.bookmark.BookmarkDao
import javax.inject.Inject

class BookmarkLocalDataSource @Inject constructor(
   private val dao: BookmarkDao
) {
   suspend fun insert(bookmark: Bookmark) = dao.insert(bookmark)
   suspend fun delete(bookmark: Bookmark) = dao.delete(bookmark)

   suspend fun getBookmark(dataSource: DataSource, id: String) = dao.getBookmark(dataSource, id)
   fun observeBookmarks() = dao.observeBookmarks()
   fun observeBookmarks(dataSource: DataSource) = dao.observeBookmarks(dataSource)
   fun observeBookmark(dataSource: DataSource, id: String) = dao.observeBookmark(dataSource, id)
}