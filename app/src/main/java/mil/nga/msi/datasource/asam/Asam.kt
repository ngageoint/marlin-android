package mil.nga.msi.datasource.asam

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
   val longitude: Double,

   @ColumnInfo(name = "position")
   val position: String,

   @ColumnInfo(name = "navigation_area")
   val navigationArea: String,

   @ColumnInfo(name = "subregion")
   val subregion: String,

   @ColumnInfo(name = "description")
   val description: String,
) {

   @ColumnInfo(name = "hostility")
   var hostility: String? = null

   @ColumnInfo(name = "victim")
   var victim: String? = null

}