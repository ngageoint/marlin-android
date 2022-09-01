package mil.nga.msi.datasource.dgpsstation

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DgpsStationDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(dgpsStation: DgpsStation)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(lights: List<DgpsStation>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(light: DgpsStation)

   @Query("SELECT * FROM lights")
   suspend fun getDgpsStations(): List<DgpsStation>

   @Query("SELECT * FROM dgps_stations WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getDgpsStations(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<DgpsStation>

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestDgpsStation(volumeNumber: String): DgpsStation?

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber AND feature_number = :featureNumber")
   suspend fun getDgpsStation(volumeNumber: String, featureNumber: Int): DgpsStation?

   @Query("SELECT * FROM dgps_stations WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY feature_number")
   fun observeDgpsStation(volumeNumber: String, featureNumber: Int): Flow<DgpsStation>

   @Query("SELECT * FROM dgps_stations ORDER BY section_header ASC, feature_number ASC")
   @RewriteQueriesToDropUnusedColumns
   fun getDgpsListItems(): PagingSource<Int, DgpsStationListItem>
}