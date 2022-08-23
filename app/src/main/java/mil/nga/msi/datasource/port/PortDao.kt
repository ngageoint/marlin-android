package mil.nga.msi.datasource.port

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import mil.nga.msi.datasource.light.Light

@Dao
interface PortDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(port: Port)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(ports: List<Port>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(port: Port)

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   fun observePort(portNumber: Int): LiveData<Port>

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
   fun getPortListItems(): PagingSource<Int, PortListItem>
}