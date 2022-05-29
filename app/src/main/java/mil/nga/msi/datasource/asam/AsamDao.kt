package mil.nga.msi.datasource.asam

import androidx.room.*


@Dao
interface AsamDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asam: Asam)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(asams: List<Asam>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(asam: Asam)

   @Query("SELECT * FROM asam")
   suspend fun asams(): List<Asam>

   @Query("SELECT * FROM asam ORDER BY date DESC LIMIT 1")
   suspend fun latestAsam(): Asam?
}