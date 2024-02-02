package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo

class RouteMapItem(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "geoJson") val geoJson: String?
)