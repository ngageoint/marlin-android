package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "navigational_warnings")
data class NavigationalWarning(
   @PrimaryKey
   @ColumnInfo(name = "number")
   val number: Int,

   @ColumnInfo(name = "year")
   val year: Int,

   @ColumnInfo(name = "issueDate")
   var issueDate: Date? = null
) {
   @ColumnInfo(name = "subregion")
   var subregions: List<String>? = emptyList()

   @ColumnInfo(name = "text")
   var text: String? = null

   @ColumnInfo(name = "status")
   var status: String? = null

   @ColumnInfo(name = "authority")
   var authority: String? = null

   @ColumnInfo(name = "cancelDate")
   var cancelDate: Date? = null

   @ColumnInfo(name = "cancel_navigation_area")
   var cancelNavigationArea: String? = null

   @ColumnInfo(name = "cancel_year")
   var cancelYear: Int? = null

   @ColumnInfo(name = "cancel_number")
   var cancelNumber: Int? = null
}