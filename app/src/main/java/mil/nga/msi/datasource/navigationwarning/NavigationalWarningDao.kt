package mil.nga.msi.datasource.navigationwarning

import androidx.paging.PagingSource
import androidx.room.*
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.datasource.modu.Modu

@Dao
interface NavigationalWarningDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarning: NavigationalWarning)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(navigationalWarning: NavigationalWarning)

   @Query("SELECT * FROM navigational_warnings WHERE number = :number")
   suspend fun getNavigationalWarning(number: Int): NavigationalWarning?

   @Query("SELECT * FROM navigational_warnings")
   suspend fun getNavigationalWarnings(): List<NavigationalWarning>

   @Query("SELECT * FROM navigational_warnings ORDER BY issue_date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getNavigationalWarningListItems(): PagingSource<Int, NavigationalWarningListItem>
}