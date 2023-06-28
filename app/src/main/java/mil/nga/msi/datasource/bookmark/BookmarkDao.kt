package mil.nga.msi.datasource.bookmark

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mil.nga.msi.datasource.DataSource

@Dao
interface BookmarkDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(bookmark: Bookmark)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(bookmark: Bookmark): Int

   @Delete
   suspend fun delete(bookmark: Bookmark)

   @Query("SELECT * from bookmarks ORDER BY timestamp")
   fun observeBookmarks(): Flow<List<Bookmark>>

   @Query("SELECT * from bookmarks WHERE data_source = :datasource ORDER BY timestamp")
   fun observeBookmarks(datasource: DataSource): Flow<List<Bookmark>>

   @Query("SELECT * from bookmarks WHERE data_source = :datasource AND id = :id")
   fun observeBookmark(datasource: DataSource, id: String): Flow<Bookmark>

   @Query("SELECT * from bookmarks WHERE data_source = :datasource AND id = :id ORDER BY timestamp")
   suspend fun getBookmark(datasource: DataSource, id: String): Bookmark
}