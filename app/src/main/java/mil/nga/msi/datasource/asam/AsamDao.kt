package mil.nga.msi.datasource.asam

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AsamDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asam: Asam)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asams: List<Asam>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(asam: Asam)

   @Query("SELECT * FROM asam")
   fun observeAsams(): Flow<List<Asam>>

   @Query("SELECT * FROM asam WHERE reference = :reference")
   fun observeAsam(reference: String): LiveData<Asam>

   @Query("SELECT * FROM asam")
   suspend fun getAsams(): List<Asam>

   @Query("SELECT * FROM asam WHERE reference = :reference")
   suspend fun getAsamByReference(reference: String): Asam?

   @Query("SELECT * FROM asam ORDER BY date DESC LIMIT 1")
   suspend fun getLatestAsam(): Asam?

   @Query("SELECT * FROM asam ORDER BY date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getAsamListItems(): PagingSource<Int, AsamListItem>

   @Query("SELECT * FROM asam")
   @RewriteQueriesToDropUnusedColumns
   fun observeAsamMapItems(): Flow<List<AsamMapItem>>
}