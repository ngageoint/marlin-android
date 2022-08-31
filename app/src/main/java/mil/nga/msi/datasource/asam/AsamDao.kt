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

   @Query("SELECT * FROM asams")
   fun observeAsams(): Flow<List<Asam>>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   fun observeAsam(reference: String): LiveData<Asam>

   @Query("SELECT * FROM asams")
   suspend fun getAsams(): List<Asam>

   @Query("SELECT * FROM asams WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getAsams(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Asam>

   @Query("SELECT * FROM asams WHERE reference = :reference")
   suspend fun getAsam(reference: String): Asam?

   @Query("SELECT * FROM asams ORDER BY date DESC LIMIT 1")
   suspend fun getLatestAsam(): Asam?

   @Query("SELECT * FROM asams ORDER BY date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getAsamListItems(): PagingSource<Int, AsamListItem>

   @Query("SELECT * FROM asams")
   @RewriteQueriesToDropUnusedColumns
   fun observeAsamMapItems(): Flow<List<AsamMapItem>>
}