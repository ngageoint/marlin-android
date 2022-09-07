package mil.nga.msi.datasource.radiobeacon

import androidx.room.ColumnInfo

data class RadioBeaconMapItem(
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "feature_number") val featureNumber: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double
)