package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.text.SimpleDateFormat
import java.util.*

enum class NavigationArea(val code: String, val title: String) {
   HYDROARC("C", "HYDROARC"),
   HYDROLANT("A", "HYDROLANT"),
   HYDROPAC("P", "HYDROPAC"),
   NAVAREA_IV("4", "NAVAREA IV"),
   NAVAREA_XII("12", "NAVAREA XII"),
   SPECIAL_WARNING("S", "Special Warning");

   companion object {
      fun fromCode(code: String): NavigationArea? {
         return values().find { it.code == code }
      }
   }
}

@Entity(
   tableName = "navigational_warnings",
   primaryKeys = ["number", "year", "navigation_area"]
)
data class NavigationalWarning(
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
   var subregions: List<String>? = emptyList()

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