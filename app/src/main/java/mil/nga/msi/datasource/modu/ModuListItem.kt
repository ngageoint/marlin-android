package mil.nga.msi.datasource.modu

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import java.util.*

data class ModuListItem(
   @ColumnInfo(name = "name") val name: String,
   @ColumnInfo(name = "date") val date: Date,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "rig_status") val rigStatus: String?,
   @ColumnInfo(name = "special_status") val specialStatus: String?,
) {
   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}