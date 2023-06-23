package mil.nga.msi.datasource.asam

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AsamDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asam: Asam)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asams: List<Asam>): List<Long>

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(asam: Asam): Int

   @Query("SELECT COUNT(*) from asams")
   fun count(): Int

   @Query("SELECT * FROM asams")
   @RewriteQueriesToDropUnusedColumns
   fun observeAsams(): PagingSource<Int, AsamListItem>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   fun observeAsam(reference: String): LiveData<Asam>

   @Query("SELECT * FROM asams")
   suspend fun getAsams(): List<Asam>

   @Query("SELECT * FROM asams WHERE reference IN (:references)")
   suspend fun getAsams(references: List<String>): List<Asam>

   @RawQuery(observedEntities = [Asam::class])
   @RewriteQueriesToDropUnusedColumns
   fun getAsams(query: SupportSQLiteQuery): List<Asam>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   suspend fun getAsam(reference: String): Asam?

   @RawQuery(observedEntities = [Asam::class])
   fun getAsamListItems(query: SupportSQLiteQuery): PagingSource<Int, Asam>

   @RawQuery(observedEntities = [Asam::class])
   @RewriteQueriesToDropUnusedColumns
   fun observeAsamMapItems(query: SupportSQLiteQuery): Flow<List<AsamMapItem>>

   @Query("UPDATE asams SET bookmarked = :bookmarked, bookmarkDate = :date, bookmarkNotes = :notes WHERE reference = :reference")
   suspend fun setBookmarked(reference: String, bookmarked: Boolean, date: Date? = null, notes: String? = null)

   @Query("SELECT * from asams WHERE bookmarked = 1 ORDER BY bookmarkDate")
   fun observeBookmarkedAsams(): Flow<List<Asam>>
}