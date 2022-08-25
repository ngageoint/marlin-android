package mil.nga.msi.datasource.radiobeacon

import androidx.room.*

@Dao
interface RadioBeaconDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(light: RadioBeacon)

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(lights: List<RadioBeacon>)

   @Update(onConflict = OnConflictStrategy.REPLACE)
   suspend fun update(light: RadioBeacon)

   @Query("SELECT * FROM lights")
   suspend fun getRadioBeacons(): List<RadioBeacon>

//   @Query("SELECT * FROM lights WHERE latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
//   fun getLights(
//      minLatitude: Double,
//      maxLatitude: Double,
//      minLongitude: Double,
//      maxLongitude: Double
//   ): List<Light>
//
//   @Query("SELECT * FROM lights WHERE characteristic_number = :characteristicNumber AND latitude >= :minLatitude AND latitude <= :maxLatitude AND longitude >= :minLongitude AND longitude <= :maxLongitude")
//   fun getLights(
//      minLatitude: Double,
//      maxLatitude: Double,
//      minLongitude: Double,
//      maxLongitude: Double,
//      characteristicNumber: Int
//   ): List<Light>
//
   @Query("SELECT * FROM radio_beacons WHERE volume_number = :volumeNumber ORDER BY notice_number DESC LIMIT 1")
   suspend fun getLatestRadioBeacon(volumeNumber: String): RadioBeacon?
//
//   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber AND feature_number = :featureNumber AND characteristic_number = :characteristicNumber")
//   suspend fun getLight(volumeNumber: String, featureNumber: String, characteristicNumber: Int): Light?
//
//   @Query("SELECT * FROM lights WHERE volume_number = :volumeNumber AND feature_number = :featureNumber ORDER BY characteristic_number")
//   fun observeLight(volumeNumber: String, featureNumber: String): Flow<List<Light>>
//
//   @Query("SELECT * FROM lights ORDER BY section_header ASC, feature_number ASC")
//   @RewriteQueriesToDropUnusedColumns
//   fun getLightListItems(): PagingSource<Int, LightListItem>
//
//   @Query("SELECT * FROM lights")
//   @RewriteQueriesToDropUnusedColumns
//   fun observeMapItems(): Flow<List<LightMapItem>>
}