package mil.nga.msi.datasource.light

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LightDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(light: Light)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(lights: List<Light>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(light: Light)

   @Query("SELECT * FROM lights")
   suspend fun getLights(): List<Light>

   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestLight(volumeNumber: String): Light?

   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber AND feature_number = :featureNumber AND characteristic_number = :characteristicNumber")
   suspend fun getLight(volumeNumber: String, featureNumber: String, characteristicNumber: Int): Light?

   @Query("SELECT * FROM lights ORDER BY section_header ASC, feature_number ASC")
   @RewriteQueriesToDropUnusedColumns
   fun getLightListItems(): LiveData<List<Light>>
}