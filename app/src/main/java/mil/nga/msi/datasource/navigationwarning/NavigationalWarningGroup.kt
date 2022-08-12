package mil.nga.msi.datasource.navigationwarning

import androidx.room.ColumnInfo

data class NavigationalWarningGroup(
   @ColumnInfo(name = "navigation_area") val navigationArea: NavigationArea,
   @ColumnInfo(name = "total") val total: Int,
   @ColumnInfo(name = "unread") val unread: Int
)