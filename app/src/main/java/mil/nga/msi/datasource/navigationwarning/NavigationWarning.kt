package mil.nga.msi.datasource.navigationwarning

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import mil.nga.msi.datasource.Position
import mil.nga.sf.GeometryType
import mil.nga.sf.wkt.GeometryReader
import java.text.SimpleDateFormat
import java.util.*

enum class NavigationArea(
   val code: String,
   val title: String,
   val color: Color
) {
   HYDROARC("C", "HYDROARC", Color(0xFF77DFFC)),
   HYDROLANT("A", "HYDROLANT", Color(0xFF7C91F2)),
   HYDROPAC("P", "HYDROPAC", Color(0xFFF5F481)),
   NAVAREA_IV("4", "NAVAREA IV", Color(0xFFFDBFBF)),
   NAVAREA_XII("12", "NAVAREA XII", Color(0xFF8BCC6B)),
   SPECIAL_WARNING("S", "Special Warning", Color.Unspecified),
   UNPARSED("NA", "UNPARSED LOCATIONS", Color.Transparent);

   companion object {
      fun fromCode(code: String): NavigationArea? {
         return values().find { it.code == code }
      }
   }
}

@Entity(
   tableName = "navigational_warnings",
   primaryKeys = ["number", "year", "navigation_area"],
   indices = [Index(value = ["id"], unique = true)]
)
data class NavigationalWarning(
   @ColumnInfo(name = "id")
   val id: String,

   @ColumnInfo(name = "number")
   val number: Int,

   @ColumnInfo(name = "year")
   val year: Int,

   @ColumnInfo(name = "navigation_area")
   var navigationArea: NavigationArea,

   @ColumnInfo(name = "issue_date")
   var issueDate: Date
) {

   @ColumnInfo(name = "subregions")
   var subregions: List<String>? = mutableListOf()

   @ColumnInfo(name = "text")
   var text: String? = null

   @ColumnInfo(name = "status")
   var status: String? = null

   @ColumnInfo(name = "authority")
   var authority: String? = null

   @ColumnInfo(name = "cancel_number")
   var cancelNumber: Int? = null

   @ColumnInfo(name = "cancelDate")
   var cancelDate: Date? = null

   @ColumnInfo(name = "cancel_navigation_area")
   var cancelNavigationArea: String? = null

   @ColumnInfo(name = "cancel_year")
   var cancelYear: Int? = null

   @ColumnInfo(name = "position")
   var position: Position? = null

   fun compositeKey(): String {
      return compositeKey(number, year, navigationArea)
   }

   fun bounds(): LatLngBounds? {
      val builder = LatLngBounds.builder()
      position?.locations?.forEach { location ->
         val geometry = GeometryReader.readGeometry(location.wkt)
         when (geometry.geometryType) {
            GeometryType.POINT -> {
               val centroid = geometry.centroid
               builder.include(LatLng(centroid.y, centroid.x))
            }
            GeometryType.LINESTRING, GeometryType.POLYGON -> {
               val envelope = geometry.envelope
               builder.include(LatLng(envelope.bottomLeft.y, envelope.bottomLeft.x))
               builder.include(LatLng(envelope.topLeft.y, envelope.topLeft.x))
               builder.include(LatLng(envelope.topRight.y, envelope.topRight.x))
               builder.include(LatLng(envelope.bottomRight.y, envelope.bottomRight.x))
            }
            else -> {}
         }
      }

      return try { builder.build() } catch(e: Exception) { null }
   }

   override fun toString(): String {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
      return "Navigational Warning\n\n" +
              "${dateFormat.format(issueDate)}\n\n" +
              "$navigationArea $number/$year (${subregions?.joinToString(",")})\n\n" +
              "$text\n\n" +
              "Status: $status\n" +
              "Authority: $authority\n" +
              "Cancel Date: ${dateFormat.format(issueDate)}\n" +
              "Cancel Year: $cancelNumber\n" +
              "Cancel Year: $cancelYear\n"
   }

   companion object {
      val numberComparator = Comparator<NavigationalWarning> { a, b ->
         a.number.compareTo(b.number)
      }

      fun compositeKey(number: Int, year: Int, navigationArea: NavigationArea): String {
         return "$number--$year--${navigationArea.name}"
      }
   }
}