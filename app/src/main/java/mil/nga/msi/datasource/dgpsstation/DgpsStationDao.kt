package mil.nga.msi.datasource.dgpsstation

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface DgpsStationDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(dgpsStation: DgpsStation)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(dgpsStations: List<DgpsStation>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(dgpsStation: DgpsStation)

   @Query("SELECT COUNT(*) from dgps_stations")
   fun count(): Int

   @Query("SELECT * FROM dgps_stations")
   suspend fun getDgpsStations(): List<DgpsStation>

   @RawQuery(observedEntities = [DgpsStation::class])
   @RewriteQueriesToDropUnusedColumns
   fun getDgpsStations(query: SupportSQLiteQuery): List<DgpsStation>

   @Query("SELECT * FROM dgps_stations WHERE id IN (:ids)")
   suspend fun getDgpsStations(ids: List<String>): List<DgpsStation>

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestDgpsStation(volumeNumber: String): DgpsStation?

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber AND feature_number = :featureNumber")
   suspend fun getDgpsStation(volumeNumber: String, featureNumber: Float): DgpsStation?

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY feature_number")
   fun observeDgpsStation(volumeNumber: String, featureNumber: Float): Flow<DgpsStation>

   @RawQuery(observedEntities = [DgpsStation::class])
   fun observeDgpsStationListItems(query: SupportSQLiteQuery): PagingSource<Int, DgpsStation>

   @RawQuery(observedEntities = [DgpsStation::class])
   @RewriteQueriesToDropUnusedColumns
   fun observeDgpsStationMapItems(query: SupportSQLiteQuery): Flow<List<DgpsStationMapItem>>
}