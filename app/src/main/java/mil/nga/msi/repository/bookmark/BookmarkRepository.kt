package mil.nga.msi.repository.bookmark

import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.bookmark.Bookmark
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
   private val localDataSource: BookmarkLocalDataSource
) {
   suspend fun insert(bookmark: Bookmark) = localDataSource.insert(bookmark)
   suspend fun delete(bookmark: Bookmark) = localDataSource.delete(bookmark)

   suspend fun getBookmark(dataSource: DataSource, id: String) = localDataSource.getBookmark(dataSource, id)

   fun observeBookmarks() = localDataSource.observeBookmarks()
   fun observeBookmarks(dataSource: DataSource) = localDataSource.observeBookmarks(dataSource)
   fun observeBookmark(dataSource: DataSource, id: String) = localDataSource.observeBookmark(dataSource, id)

}