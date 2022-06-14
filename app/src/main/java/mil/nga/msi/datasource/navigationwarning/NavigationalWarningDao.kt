package mil.nga.msi.datasource.navigationwarning

import androidx.room.*
import mil.nga.msi.datasource.modu.Modu

@Dao
interface NavigationalWarningDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarning: NavigationalWarning)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(navigationalWarning: NavigationalWarning)

   @Query("SELECT * FROM navigational_warnings")
   suspend fun getNavigationalWarnings(): List<NavigationalWarning>
}