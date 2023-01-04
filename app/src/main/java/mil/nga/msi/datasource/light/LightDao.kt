package mil.nga.msi.datasource.light

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface LightDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(light: Light)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(lights: List<Light>): List<Long>

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(light: Light): Int

   @Query("SELECT COUNT(*) from lights")
   fun count(): Int

   @Query("SELECT * FROM lights")
   suspend fun getLights(): List<Light>

   @RawQuery(observedEntities = [Light::class])
   @RewriteQueriesToDropUnusedColumns
   fun getLights(query: SupportSQLiteQuery): List<Light>

   @Query("SELECT * FROM lights WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<Light>

   @Query("SELECT * FROM lights WHERE characteristic_number = :characteristicNumber AND latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getLights(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double,
      characteristicNumber: Int
   ): List<Light>

   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestLight(volumeNumber: String): Light?

   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber AND feature_number = :featureNumber AND characteristic_number = :characteristicNumber")
   suspend fun getLight(volumeNumber: String, featureNumber: String, characteristicNumber: Int): Light?

   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY characteristic_number")
   fun observeLight(volumeNumber: String, featureNumber: String): Flow<List<Light>>

   @RawQuery(observedEntities = [Light::class])
   fun observeLightListItems(query: SupportSQLiteQuery): PagingSource<Int, Light>

   @RawQuery(observedEntities = [Light::class])
   @RewriteQueriesToDropUnusedColumns
   fun observeLightMapItems(query: SupportSQLiteQuery): Flow<List<LightMapItem>>

   @Query("SELECT * FROM lights WHERE id IN (:ids)")
   suspend fun existingLights(ids: List<String>): List<Light>
}