package mil.nga.msi.ui.route.create

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.maps.android.compose.TileOverlayState
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.map.RouteCreationTileRepository
import mil.nga.msi.repository.route.RouteCreationRepository
import mil.nga.msi.ui.map.overlay.RouteTileProvider
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

    val tileProvider = RouteTileProvider(application, RouteCreationTileRepository(routeCreationRepository))

    init {

        val newRoute = Route(
            name = name.value,
            createdTime = Date(),
            updatedTime = Date()
        )
        routeCreationRepository.setRoute(newRoute)
    }

    val locationProvider = locationPolicy.bestLocationProvider

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun removeWaypoint(waypoint: RouteWaypoint) = routeCreationRepository.removeWaypoint(waypoint)
    fun moveWaypoint(fromIndex: Int, toIndex: Int): List<RouteWaypoint> = routeCreationRepository.moveWaypoint(fromIndex, toIndex)

    fun setName(name: String) {
        this.name.value = name
        route.value?.name = name
    }

    private fun clearWaypoints() = routeCreationRepository.clearWaypoints()

    suspend fun saveRoute() = routeCreationRepository.saveRoute()
}