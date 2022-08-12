package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo
import com.google.android.gms.maps.model.LatLng
import mil.nga.msi.coordinate.DMS
import java.util.*

data class NavigationalWarningListItem(
   @ColumnInfo(name = "number") val number: Int,
   @ColumnInfo(name = "year") val year: Int,
   @ColumnInfo(name = "issue_date") val issueDate: Date,
   @ColumnInfo(name = "navigation_area") val navigationArea: NavigationArea,
   @ColumnInfo(name = "subregions") val subregions: List<String>?,
   @ColumnInfo(name = "text") val text: String?
)