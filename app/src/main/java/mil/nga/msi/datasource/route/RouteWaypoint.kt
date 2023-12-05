package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.datasource.radiobeacon.RadioBeacon

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
            return Json.decodeFromString<Asam>(it)
        }
        return null
    }

    fun getModu(): Modu? {
        if (dataSource != DataSource.MODU) {
            return null
        }
        json?.let {
            return Json.decodeFromString<Modu>(it)
        }
        return null
    }

    fun getNavigationalWarning(): NavigationalWarning? {
        if (dataSource != DataSource.NAVIGATION_WARNING) {
            return null
        }
        json?.let {
            return Json.decodeFromString<NavigationalWarning>(it)
        }
        return null
    }

    fun getLight(): Light? {
        if (dataSource != DataSource.LIGHT) {
            return null
        }
        json?.let {
            return Json.decodeFromString<Light>(it)
        }
        return null
    }

    fun getPort(): Port? {
        if (dataSource != DataSource.PORT) {
            return null
        }
        json?.let {
            return Json.decodeFromString<Port>(it)
        }
        return null
    }

    fun getRadioBeacon(): RadioBeacon? {
        if (dataSource != DataSource.RADIO_BEACON) {
            return null
        }
        json?.let {
            return Json.decodeFromString<RadioBeacon>(it)
        }
        return null
    }

    fun getDGPSStation(): DgpsStation? {
        if (dataSource != DataSource.DGPS_STATION) {
            return null
        }
        json?.let {
            return Json.decodeFromString<DgpsStation>(it)
        }
        return null
    }
}
