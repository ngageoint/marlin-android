package mil.nga.msi.datasource.modu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RigStatus { ACTIVE, INACTIVE }

@Entity(tableName = "modus")
data class Modu(
   @PrimaryKey
   @ColumnInfo(name = "name")
   val name: String,

   @ColumnInfo(name = "date")
   val date: Date,

   @ColumnInfo(name = "latitude")
   val latitude: Double,

   @ColumnInfo(name = "longitude")
   val longitude: Double
) {
   @ColumnInfo(name = "rig_status")
   var rigStatus: RigStatus? = null

   @ColumnInfo(name = "special_status")
   var specialStatus: String? = null

   @ColumnInfo(name = "distance")
   var distance: Double? = null

   @ColumnInfo(name = "position")
   var position: String? = null

   @ColumnInfo(name = "navigation_area")
   var navigationArea: String? = null

   @ColumnInfo(name = "region")
   var region: String? = null

   @ColumnInfo(name = "subregion")
   var subregion: String? = null

   @Transient
   val latLng = LatLng(latitude, longitude)

   override fun toString(): String {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
      return "MODU\n\n" +
              "Name: $name\n" +
              "Date: ${dateFormat.format(date)}\n" +
              "Latitude: $latitude\n" +
              "Longitude: $longitude\n" +
              "Position: $position\n" +
              "Rig Status: $rigStatus\n" +
              "Special Status: $specialStatus\n" +
              "distance: $distance\n" +
              "Navigation Area: $navigationArea\n" +
              "Region: $region\n" +
              "Sub Region: $subregion\n"
   }
}