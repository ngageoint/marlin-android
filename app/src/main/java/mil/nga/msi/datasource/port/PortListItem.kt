package mil.nga.msi.datasource.port

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS

data class PortListItem(
   @ColumnInfo(name = "port_number") val portNumber: Int,
   @ColumnInfo(name = "port_name") val portName: String,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "alternate_name") val alternateName: String?,
) {
   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}