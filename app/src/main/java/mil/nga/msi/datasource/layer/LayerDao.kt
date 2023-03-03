package mil.nga.msi.datasource.layer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LayerDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(layer: Layer)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(layer: Layer)

   @Delete
   suspend fun delete(layer: Layer)

   @Query("UPDATE layers SET visible = :enabled WHERE id = :id")
   suspend fun enable(id: Long, enabled: Boolean)

   @Query("SELECT COUNT(*) from layers")
   fun count(): Int

   @Query("SELECT * FROM layers")
   fun observeLayers(): Flow<List<Layer>>

   @Query("SELECT * FROM layers WHERE visible = 1")
   fun observeVisibleLayers(): Flow<List<Layer>>
}

