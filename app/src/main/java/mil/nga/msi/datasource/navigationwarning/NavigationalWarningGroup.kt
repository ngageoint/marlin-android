package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo
import java.util.*

data class NavigationalWarningGroup(
   @ColumnInfo(name = "count") val count: Int,
   @ColumnInfo(name = "navigation_area") val navigationArea: NavigationArea
)