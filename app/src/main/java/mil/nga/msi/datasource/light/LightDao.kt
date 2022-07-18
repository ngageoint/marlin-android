package mil.nga.msi.datasource.light

import androidx.room.*

@Dao
interface LightDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(light: Light)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(lights: List<Light>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(light: Light)
}