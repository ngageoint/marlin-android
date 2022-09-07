package mil.nga.msi.datasource.port

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

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   fun observePort(portNumber: Int): Flow<Port>

   @Query("SELECT * FROM ports")
   suspend fun getPorts(): List<Port>

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   suspend fun getPort(portNumber: Int): Port?

   @Query("SELECT * FROM ports WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Port>

   @Query("SELECT * FROM ports ORDER BY port_name ASC")
   @RewriteQueriesToDropUnusedColumns
   fun observePortListItems(): PagingSource<Int, PortListItem>

   @Query("SELECT * FROM ports ORDER BY port_number")
   fun observePortMapItems(): Flow<List<PortMapItem>>
}