package mil.nga.msi.datasource.navigationwarning

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.serialization.Serializable
import mil.nga.msi.datasource.port.types.EnumerationType
import mil.nga.msi.serializer.DateSerializer
import mil.nga.sf.geojson.Feature
import mil.nga.sf.geojson.FeatureConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NavigationArea(
   val code: String,
   override val title: String,
   val color: Color
): EnumerationType {
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

      fun fromValue(value: String?): NavigationArea? {
         return  try {
            NavigationArea.valueOf(value!!)
         } catch (e: Exception) {
            null
         }
      }
   }
}

@Serializable
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

   @Serializable(DateSerializer::class)
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

   @Serializable(DateSerializer::class)
   @ColumnInfo(name = "cancelDate")
   var cancelDate: Date? = null

   @ColumnInfo(name = "cancel_navigation_area")
   var cancelNavigationArea: String? = null

   @ColumnInfo(name = "cancel_year")
   var cancelYear: Int? = null

   @ColumnInfo(name = "geoJson")
   var geoJson: String? = null

   @ColumnInfo(name = "min_latitude")
   var minLatitude: Double? = null

   @ColumnInfo(name = "min_longitude")
   var minLongitude: Double? = null

   @ColumnInfo(name = "max_latitude")
   var maxLatitude: Double? = null

   @ColumnInfo(name = "max_longitude")
   var maxLongitude: Double? = null

   fun getFeatures(): List<Feature> {
      return geoJson?.let {
         FeatureConverter.toFeatureCollection(it)?.features ?: emptyList()
      } ?: emptyList()
   }

   fun bounds(): LatLngBounds? {
      val builder = LatLngBounds.builder()

      val minY = minLatitude
      val minX = minLongitude
      if (minY != null && minX != null) {
         builder.include(LatLng(minY, minX))
      }

      val maxY = maxLatitude
      val maxX = maxLongitude
      if (maxY != null && maxX != null) {
         builder.include(LatLng(maxY, maxX))
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
   }
}