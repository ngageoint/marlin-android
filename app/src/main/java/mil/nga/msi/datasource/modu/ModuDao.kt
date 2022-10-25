package mil.nga.msi.datasource.modu

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(modu: Modu)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(modus: List<Modu>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(modu: Modu)

   @Query("SELECT COUNT(*) from modus")
   fun count(): Int

   @Query("SELECT * FROM modus")
   fun observeModus(): Flow<List<Modu>>

   @RawQuery(observedEntities = [Modu::class])
   @RewriteQueriesToDropUnusedColumns
   fun getModus(query: SupportSQLiteQuery): List<Modu>

   @Query("SELECT * FROM modus WHERE name = :name")
   fun observeModu(name: String): LiveData<Modu>

   @Query("SELECT * FROM modus")
   suspend fun getModus(): List<Modu>

   @Query("SELECT * FROM modus WHERE name = :name")
   suspend fun getModu(name: String): Modu?

   @Query("SELECT * FROM modus ORDER BY date DESC LIMIT 1")
   suspend fun getLatestModu(): Modu?

   @RawQuery(observedEntities = [Modu::class])
   fun getModuListItems(query: SupportSQLiteQuery): PagingSource<Int, ModuListItem>

   @Query("SELECT * FROM modus ORDER BY name")
   @RewriteQueriesToDropUnusedColumns
   fun observeModuMapItems(): Flow<List<ModuMapItem>>

   @Query("SELECT * FROM modus WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getModus(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Modu>

   @Query("SELECT * FROM modus WHERE name IN (:names)")
   suspend fun existingModus(names: List<String>): List<Modu>
}

