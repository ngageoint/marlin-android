package mil.nga.msi.datasource.port

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface PortDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(port: Port)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(ports: List<Port>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(port: Port)

   @Query("SELECT COUNT(*) from ports")
   fun count(): Int

   @RawQuery
   @RewriteQueriesToDropUnusedColumns
   suspend fun count(query: SupportSQLiteQuery): Int

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   fun observePort(portNumber: Int): Flow<Port>

   @Query("SELECT * FROM ports")
   suspend fun getPorts(): List<Port>

   @RawQuery(observedEntities = [Port::class])
   @RewriteQueriesToDropUnusedColumns
   fun getPorts(query: SupportSQLiteQuery): List<Port>

   @Query("SELECT * FROM ports WHERE port_number IN (:portNumbers)")
   suspend fun getPorts(portNumbers: List<Int>): List<Port>

   @Query("SELECT * FROM ports WHERE port_number = :portNumber")
   suspend fun getPort(portNumber: Int): Port?

   @Query("SELECT * FROM ports WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getPorts(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Port>

   @RawQuery(observedEntities = [Port::class])
   fun observePortListItems(query: SupportSQLiteQuery): PagingSource<Int, Port>

   @RawQuery(observedEntities = [Port::class])
   @RewriteQueriesToDropUnusedColumns
   fun observePortMapItems(query: SupportSQLiteQuery): Flow<List<PortMapItem>>
}