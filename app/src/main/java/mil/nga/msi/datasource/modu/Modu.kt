package mil.nga.msi.datasource.modu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import java.util.*

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
   val dms = DMS.from(LatLng(latitude, longitude))
}