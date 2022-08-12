package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo

data class AsamMapItem(
   @ColumnInfo(name = "reference") val reference: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double
)