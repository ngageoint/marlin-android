package mil.nga.msi.datasource.port

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PortDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(port: Port)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(ports: List<Port>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(port: Port)

//   @Query("SELECT * FROM asams")
//   fun observeAsams(): Flow<List<Asam>>
//
//   @Query("SELECT * FROM asams WHERE reference = :reference")
//   fun observeAsam(reference: String): LiveData<Asam>
//
   @Query("SELECT * FROM ports")
   suspend fun getPorts(): List<Port>
//
//   @Query("SELECT * FROM asams WHERE reference = :reference")
//   suspend fun getAsam(reference: String): Asam?
//
//   @Query("SELECT * FROM asams ORDER BY date DESC LIMIT 1")
//   suspend fun getLatestAsam(): Asam?
//
//   @Query("SELECT * FROM asams ORDER BY date DESC")
//   @RewriteQueriesToDropUnusedColumns
//   fun getAsamListItems(): PagingSource<Int, AsamListItem>
//
//   @Query("SELECT * FROM asams")
//   @RewriteQueriesToDropUnusedColumns
//   fun observeAsamMapItems(): Flow<List<AsamMapItem>>
}