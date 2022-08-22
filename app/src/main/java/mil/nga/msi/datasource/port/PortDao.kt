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
   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   fun observePort(portNumber: Int): LiveData<Port>

   @Query("SELECT * FROM ports")
   suspend fun getPorts(): List<Port>

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   suspend fun getPort(portNumber: Int): Port?
//
//   @Query("SELECT * FROM asams ORDER BY date DESC LIMIT 1")
//   suspend fun getLatestAsam(): Asam?
//
   @Query("SELECT * FROM ports ORDER BY port_name ASC")
   @RewriteQueriesToDropUnusedColumns
   fun getPortListItems(): PagingSource<Int, PortListItem>

//   @Query("SELECT * FROM asams")
//   @RewriteQueriesToDropUnusedColumns
//   fun observeAsamMapItems(): Flow<List<AsamMapItem>>
}