package mil.nga.msi.datasource.radiobeacon

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RadioBeaconDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(beacon: RadioBeacon)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(beacons: List<RadioBeacon>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(beacon: RadioBeacon)

   @Query("SELECT COUNT(*) from radio_beacons")
   fun count(): Int

   @Query("SELECT * FROM radio_beacons")
   suspend fun getRadioBeacons(): List<RadioBeacon>

   @Query("SELECT * FROM radio_beacons WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
   fun getRadioBeacons(
      minLatitude: Double,
      maxLatitude: Double,
      minLongitude: Double,
      maxLongitude: Double
   ): List<RadioBeacon>

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestRadioBeacon(volumeNumber: String): RadioBeacon?

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber AND feature_number = :featureNumber")
   suspend fun getRadioBeacon(volumeNumber: String, featureNumber: String): RadioBeacon?

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY feature_number")
   fun observeRadioBeacon(volumeNumber: String, featureNumber: String): Flow<RadioBeacon>

   @Query("SELECT * FROM radio_beacons ORDER BY section_header ASC, feature_number ASC")
   @RewriteQueriesToDropUnusedColumns
   fun observeRadioBeaconListItems(): PagingSource<Int, RadioBeaconListItem>

   @Query("SELECT * FROM radio_beacons ORDER BY volume_number, feature_number")
   @RewriteQueriesToDropUnusedColumns
   fun observeRadioBeaconMapItems(): Flow<List<RadioBeaconMapItem>>

   @Query("SELECT * FROM radio_beacons WHERE id IN (:ids)")
   suspend fun existingRadioBeacons(ids: List<String>): List<RadioBeacon>
}