package mil.nga.msi.datasource.dgpsstation

import androidx.room.ColumnInfo

data class DgpsStationMapItem(
   @ColumnInfo(name = "feature_number") val featureNumber: Int,
   @ColumnInfo(name = "volume_number") val volumeNumber: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
)