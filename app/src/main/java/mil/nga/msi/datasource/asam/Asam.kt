package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "asams")
data class Asam(
   @PrimaryKey
   @ColumnInfo(name = "reference")
   val reference: String,

   @ColumnInfo(name = "date")
   val date: Date,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double,

   @ColumnInfo(name = "position")
   var position: String? = null,

   @ColumnInfo(name = "navigation_area")
   var navigationArea: String? = null,

   @ColumnInfo(name = "subregion")
   var subregion: String? = null,

   @ColumnInfo(name = "description")
   var description: String? = null,

   @ColumnInfo(name = "hostility")
   var hostility: String? = null,

   @ColumnInfo(name = "victim")
   var victim: String? = null
) {
   @Transient
   val latLng = LatLng(latitude, longitude)

   override fun toString(): String {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      return "ASAM\n\n" +
              "  Reference: $reference\n" +
              "  Date: ${dateFormat.format(date)}\n" +
              "  Latitude: $latitude\n" +
              "  Longitude: $longitude\n" +
              "  Navigate Area: $navigationArea\n" +
              "  Subregion: $subregion\n" +
              "  Description: $description\n" +
              "  Hostility: $hostility\n" +
              "  Victim: $victim"
   }
}