package mil.nga.msi.datasource.navigationwarning

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import mil.nga.msi.datasource.asam.Asam

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

   @Query("SELECT * FROM navigational_warnings ORDER BY issue_date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getNavigationalWarningListItems(): PagingSource<Int, NavigationalWarningListItem>

   @Query("DELETE FROM navigational_warnings WHERE number IN (:numbers)")
   suspend fun deleteNavigationalWarnings(numbers: List<Int>)
}