package mil.nga.msi.datasource.radiobeacon

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
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

   @RawQuery(observedEntities = [RadioBeacon::class])
   @RewriteQueriesToDropUnusedColumns
   fun getRadioBeacons(query: SupportSQLiteQuery): List<RadioBeacon>

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestRadioBeacon(volumeNumber: String): RadioBeacon?

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber AND feature_number = :featureNumber")
   suspend fun getRadioBeacon(volumeNumber: String, featureNumber: String): RadioBeacon?

   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY feature_number")
   fun observeRadioBeacon(volumeNumber: String, featureNumber: String): Flow<RadioBeacon>

   @RawQuery(observedEntities = [RadioBeacon::class])
   fun observeRadioBeaconListItems(query: SupportSQLiteQuery): PagingSource<Int, RadioBeaconListItem>

   @RawQuery(observedEntities = [RadioBeacon::class])
   @RewriteQueriesToDropUnusedColumns
   fun observeRadioBeaconMapItems(query: SupportSQLiteQuery): Flow<List<RadioBeaconMapItem>>

   @Query("SELECT * FROM radio_beacons WHERE id IN (:ids)")
   suspend fun existingRadioBeacons(ids: List<String>): List<RadioBeacon>
}