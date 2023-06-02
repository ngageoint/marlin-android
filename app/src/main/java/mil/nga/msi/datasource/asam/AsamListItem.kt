package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import java.util.Date

data class AsamListItem(
   @ColumnInfo(name = "reference") val reference: String,
   @ColumnInfo(name = "date") val date: Date,
   @ColumnInfo(name = "latitude") val latitude: Double,
   @ColumnInfo(name = "longitude") val longitude: Double,
   @ColumnInfo(name = "hostility") val hostility: String?,
   @ColumnInfo(name = "victim") val victim: String?,
   @ColumnInfo(name = "description") val description: String?
) {
   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}