package mil.nga.msi.datasource.port

import androidx.room.ColumnInfo

data class PortMapItem(
   @ColumnInfo(name = "port_number") val portNumber: Int,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double
)