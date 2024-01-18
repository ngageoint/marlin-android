package mil.nga.msi.datasource.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
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

data class WaypointTitleAndCoordinate(val title: String, val coordinate: LatLng?)

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

    fun getTitleAndCoordinate(): WaypointTitleAndCoordinate {
        when (dataSource) {
            DataSource.ASAM -> {
                getAsam()?.let { asam ->
                    val header = listOfNotNull(asam.hostility, asam.victim)
                    if (header.isNotEmpty()) {
                        return WaypointTitleAndCoordinate(
                            header.joinToString(": "),
                            asam.latLng
                        )
                    } else {
                        return WaypointTitleAndCoordinate(
                            "ASAM",
                            asam.latLng
                        )
                    }
                }
                return WaypointTitleAndCoordinate(
                    "ASAM",
                    null
                )
            }

            DataSource.MODU -> {
                getModu()?.let { modu ->
                    return WaypointTitleAndCoordinate(modu.name, modu.latLng)
                }
                return WaypointTitleAndCoordinate("MODU", null)
            }

            DataSource.NAVIGATION_WARNING -> {
                getNavigationalWarning()?.let { warning ->
                    val identifier = "${warning.number}/${warning.year}"
                    val subregions = warning.subregions?.joinToString(",")
                        ?.let { "($it)" }
                    val header = listOfNotNull(
                        warning.navigationArea.title,
                        identifier,
                        subregions
                    ).joinToString(" ")
                    return WaypointTitleAndCoordinate(header, warning.latLng)
                }
                return WaypointTitleAndCoordinate("NAVIGATIONAL WARNING", null)
            }

            DataSource.LIGHT -> {
                getLight()?.let { light ->
                    val name = light.name ?: "Light"
                    return WaypointTitleAndCoordinate(name, light.latLng)
                }
                return WaypointTitleAndCoordinate("LIGHT", null)
            }

            DataSource.PORT -> {
                getPort()?.let { port ->
                    return WaypointTitleAndCoordinate(port.portName, port.latLng)
                }
                return WaypointTitleAndCoordinate("PORT", null)
            }

            DataSource.RADIO_BEACON -> {
                getRadioBeacon()?.let { beacon ->
                    val name = beacon.name ?: "Radio Beacon"
                    return WaypointTitleAndCoordinate(name, beacon.latLng)
                }
            }

            DataSource.DGPS_STATION -> {
                getDGPSStation()?.let { dgps ->
                    val name = dgps.name ?: "DGPS Station"
                    return WaypointTitleAndCoordinate(name, dgps.latLng)
                }
            }
            DataSource.ROUTE_WAYPOINT -> {
                val split = itemKey.split(";")
                val title = split.get(0)
                val latitude = split.get(1).toDoubleOrNull() ?: 0.0
                val longitude = split.get(2).toDoubleOrNull() ?: 0.0
                return WaypointTitleAndCoordinate(title, LatLng(latitude,longitude))
            }

            DataSource.ELECTRONIC_PUBLICATION -> TODO()
            DataSource.NOTICE_TO_MARINERS -> TODO()
            DataSource.BOOKMARK -> TODO()
            DataSource.ROUTE -> TODO()
            DataSource.GEOPACKAGE -> TODO()
        }
        return WaypointTitleAndCoordinate("Marlin", null)
    }

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
