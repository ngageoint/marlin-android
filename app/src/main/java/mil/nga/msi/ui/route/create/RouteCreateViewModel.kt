package mil.nga.msi.ui.route.create

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.TileOverlayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.map.RouteCreationTileRepository
import mil.nga.msi.repository.route.RouteCreationRepository
import mil.nga.msi.ui.map.overlay.DataSourceTileProvider
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    private val application: Application,
    val locationPolicy: LocationPolicy,
    private val routeCreationRepository: RouteCreationRepository
): ViewModel() {

    var name = mutableStateOf("")
    val waypoints = routeCreationRepository.waypoints
    val route = routeCreationRepository.route

    var tileOverlayState: TileOverlayState?
        get() = routeCreationRepository.tileOverlayState
        set(value) {
            routeCreationRepository.tileOverlayState = value
        }

    val tileProvider = DataSourceTileProvider(application, RouteCreationTileRepository(routeCreationRepository))
    val location = locationPolicy.bestLocationProvider.value

    fun setRouteId(routeId: Long?) {
        if (routeId != null) {
            runBlocking {
                val route = routeCreationRepository.setRouteId(routeId)
                name.value = route?.name ?: ""
            }

        } else {
            val newRoute = Route(
                name = name.value,
                createdTime = Date(),
                updatedTime = Date()
            )
            routeCreationRepository.setRoute(newRoute)

            location?.let { location2 ->
                val waypoint = RouteWaypoint(
                    dataSource = DataSource.ROUTE_WAYPOINT,
                    itemKey = "Current Location;${location2.latitude};${location2.longitude}"
                )
                routeCreationRepository.addFirstWaypointIfEmpty(waypoint)
            }
        }
    }

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun addUserWaypoint(latLng: LatLng) {
        val waypoint = RouteWaypoint(
            dataSource = DataSource.ROUTE_WAYPOINT,
            itemKey = "User Created;${latLng.latitude};${latLng.longitude}"
        )
        routeCreationRepository.addWaypoint(waypoint)
    }

    fun removeWaypoint(waypoint: RouteWaypoint) = routeCreationRepository.removeWaypoint(waypoint)
    fun moveWaypoint(fromIndex: Int, toIndex: Int): List<RouteWaypoint> = routeCreationRepository.moveWaypoint(fromIndex, toIndex)

    fun setName(name: String) {
        this.name.value = name
        route.value?.name = name
    }

    fun clearWaypoints() = routeCreationRepository.clearWaypoints()

    suspend fun saveRoute() = routeCreationRepository.saveRoute()
}