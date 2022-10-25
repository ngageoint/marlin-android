package mil.nga.msi.datasource.asam

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

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
   fun observeAsams(): PagingSource<Int, AsamListItem>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   fun observeAsam(reference: String): LiveData<Asam>

   @Query("SELECT * FROM asams")
   suspend fun getAsams(): List<Asam>

   @RawQuery(observedEntities = [Asam::class])
   @RewriteQueriesToDropUnusedColumns
   fun getAsams(query: SupportSQLiteQuery): List<Asam>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   suspend fun getAsam(reference: String): Asam?

   @Query("SELECT * FROM asams ORDER BY date DESC LIMIT 1")
   suspend fun getLatestAsam(): Asam?

   @Query("SELECT * FROM asams ORDER BY date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getAsamListItems(): PagingSource<Int, AsamListItem>

   @RawQuery(observedEntities = [Asam::class])
   fun getAsamListItems(query: SupportSQLiteQuery): PagingSource<Int, AsamListItem>

   @RawQuery(observedEntities = [Asam::class])
   @RewriteQueriesToDropUnusedColumns
   fun observeAsamMapItems(query: SupportSQLiteQuery): Flow<List<AsamMapItem>>

   @Query("SELECT * FROM asams WHERE reference IN (:references)")
   suspend fun existingAsams(references: List<String>): List<Asam>
}