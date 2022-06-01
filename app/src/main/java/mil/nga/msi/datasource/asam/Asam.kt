package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import mil.nga.msi.coordinate.DMS
import java.util.*

@Entity(tableName = "asam")
data class Asam(
   @PrimaryKey
   @ColumnInfo(name = "reference")
   val id: String,

   @ColumnInfo(name = "date")
   val date: Date,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "position")
   var position: String? = null

   @ColumnInfo(name = "navigation_area")
   var navigationArea: String? = null

   @ColumnInfo(name = "subregion")
   var subregion: String? = null

   @ColumnInfo(name = "description")
   var description: String? = null

   @ColumnInfo(name = "hostility")
   var hostility: String? = null

   @ColumnInfo(name = "victim")
   var victim: String? = null

   @Transient
   val dms = DMS.from(LatLng(latitude, longitude))
}