package mil.nga.msi.datasource.navigationwarning

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NavigationalWarningDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarning: NavigationalWarning)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(navigationalWarning: NavigationalWarning)

   @Query("SELECT * FROM navigational_warnings WHERE number = :number AND year = :year AND navigation_area = :navigationArea")
   fun observeNavigationalWarning(number: Int, year: Int, navigationArea: NavigationArea): LiveData<NavigationalWarning>

   @Query("SELECT * FROM navigational_warnings WHERE number = :number AND year = :year AND navigation_area = :navigationArea")
   suspend fun getNavigationalWarning(number: Int, year: Int, navigationArea: NavigationArea): NavigationalWarning?

   @Query("SELECT * FROM navigational_warnings")
   suspend fun getNavigationalWarnings(): List<NavigationalWarning>

   @Query("SELECT * FROM navigational_warnings WHERE navigation_area = :navigationArea ORDER BY issue_date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getNavigationalWarningsByArea(navigationArea: NavigationArea?): PagingSource<Int, NavigationalWarningListItem>

   @Query("SELECT navigation_area, COUNT(*) AS count FROM navigational_warnings GROUP BY navigation_area")
   fun getNavigationalWarningsGroupByArea(): Flow<List<NavigationalWarningGroup>>

   @Query("DELETE FROM navigational_warnings WHERE number IN (:numbers)")
   suspend fun deleteNavigationalWarnings(numbers: List<Int>)
}