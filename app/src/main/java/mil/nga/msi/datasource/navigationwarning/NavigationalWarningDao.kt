package mil.nga.msi.datasource.navigationwarning

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NavigationalWarningDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarning: NavigationalWarning)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(navigationalWarnings: List<NavigationalWarning>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(navigationalWarning: NavigationalWarning)

   @Query("SELECT COUNT(*) from navigational_warnings")
   fun count(): Int

   @Query("SELECT * FROM navigational_warnings WHERE number = :number AND year = :year AND navigation_area = :navigationArea")
   fun observeNavigationalWarning(number: Int, year: Int, navigationArea: NavigationArea): Flow<NavigationalWarning?>

   @Query("SELECT * FROM navigational_warnings WHERE number = :number AND year = :year AND navigation_area = :navigationArea")
   suspend fun getNavigationalWarning(number: Int, year: Int, navigationArea: NavigationArea): NavigationalWarning?

   @Query("SELECT * FROM navigational_warnings")
   suspend fun getNavigationalWarnings(): List<NavigationalWarning>

   @Query("SELECT * FROM navigational_warnings")
   fun observeNavigationalWarnings(): Flow<List<NavigationalWarning>>

   @Query("SELECT * FROM navigational_warnings WHERE geoJson IS NOT NULL")
   fun observeNavigationalWarningMapItems(): Flow<List<NavigationalWarningMapItem>>

   @Query("SELECT * FROM navigational_warnings WHERE geoJson IS NULL")
   fun observeUnparsedNavigationalWarnings(): Flow<List<NavigationalWarningListItem>>

   @Query("SELECT * FROM navigational_warnings WHERE id IN (:ids)")
   suspend fun getNavigationalWarnings(ids: List<String>): List<NavigationalWarning>

   @Query("SELECT * FROM navigational_warnings WHERE navigation_area = :navigationArea ORDER BY issue_date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getNavigationalWarningsByArea(navigationArea: NavigationArea?): Flow<List<NavigationalWarningListItem>>

   @Query("SELECT navigation_area, COUNT(*) AS total," + "COUNT(CASE WHEN issue_date > CASE navigation_area " +
           "WHEN 'HYDROARC' THEN :hydroarc " +
           "WHEN 'HYDROLANT' THEN :hydrolant " +
           "WHEN 'HYDROPAC' THEN :hydropac " +
           "WHEN 'NAVAREA_IV' THEN :navareaIV " +
           "WHEN 'NAVAREA_XII' THEN :navareaXII " +
           "WHEN 'SPECIAL_WARNING' THEN :special END THEN 1 END) AS unread FROM navigational_warnings GROUP BY navigation_area;")
   fun getNavigationalWarningsByNavigationArea(
      hydroarc: Date,
      hydrolant: Date,
      hydropac: Date,
      navareaIV: Date,
      navareaXII: Date,
      special: Date
   ): Flow<List<NavigationalWarningGroup>>

   @Query("SELECT * FROM navigational_warnings WHERE navigation_area = :navigationArea AND issue_date > :date ORDER BY issue_date DESC")
   @RewriteQueriesToDropUnusedColumns
   fun getNavigationalWarningsGreaterThan(date: Date, navigationArea: NavigationArea?): Flow<List<NavigationalWarningListItem>>

   @Query("DELETE FROM navigational_warnings WHERE number IN (:numbers)")
   suspend fun deleteNavigationalWarnings(numbers: List<Int>)
}