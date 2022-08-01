package mil.nga.msi.datasource.light

import androidx.room.ColumnInfo

data class LightMapItem(
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "feature_number") val featureNumber: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double
)