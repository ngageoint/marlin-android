package mil.nga.msi.datasource.modu

import androidx.room.ColumnInfo

data class ModuMapItem(
   @ColumnInfo(name = "name") val name: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double
)