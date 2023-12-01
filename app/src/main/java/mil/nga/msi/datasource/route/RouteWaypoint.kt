package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam

@Entity(tableName = "route_waypoints")
data class RouteWaypoint(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "route_id")
    var routeId: Long = 0,

    @ColumnInfo(name = "data_source")
    val dataSource: DataSource,

    @ColumnInfo(name = "item_key")
    val itemKey: String
) {
    constructor(dataSource: DataSource, itemKey: String) : this(0, 0, dataSource, itemKey)

    @ColumnInfo(name = "json")
    var json: String? = null

    @ColumnInfo(name = "order")
    var order: Int? = null

    fun getAsam(): Asam? {
        if (dataSource != DataSource.ASAM) {
            return null
        }
        json?.let {
            val asam = Json.decodeFromString<Asam>(it)
            return asam
        }
        return null
    }
}
