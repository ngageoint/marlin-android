package mil.nga.msi.datasource.light

import androidx.room.*
import mil.nga.msi.datasource.asam.Asam

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
}