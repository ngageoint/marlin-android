package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo
import java.util.Date

data class NavigationalWarningListItem(
   @ColumnInfo(name = "id") val id: String,
   @ColumnInfo(name = "number") val number: Int,
   @ColumnInfo(name = "year") val year: Int,
   @ColumnInfo(name = "issue_date") val issueDate: Date,
   @ColumnInfo(name = "navigation_area") val navigationArea: NavigationArea,
   @ColumnInfo(name = "subregions") val subregions: List<String>? = mutableListOf(),
   @ColumnInfo(name = "text") val text: String?,
   @ColumnInfo(name = "geoJson") val geoJson: String?
)
