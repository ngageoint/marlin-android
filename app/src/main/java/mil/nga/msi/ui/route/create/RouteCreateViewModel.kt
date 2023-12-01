package mil.nga.msi.ui.route.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.route.RouteCreationRepository
import mil.nga.msi.repository.route.RouteRepository
import java.util.Date
import javax.inject.Inject

data class RouteCreateState(
    val name: String? = null,
    val route: Route? = null,
    val waypoints: List<RouteWaypoint>
)
@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    val locationPolicy: LocationPolicy,
    private val routeRepository: RouteRepository,
    private val routeCreationRepository: RouteCreationRepository
): ViewModel() {

    private val _routeCreateState = MutableLiveData<RouteCreateState?>()
    val routeCreateState: LiveData<RouteCreateState?> = _routeCreateState

    val waypoints = routeCreationRepository.waypoints

    val locationProvider = locationPolicy.bestLocationProvider

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun addWaypoint(waypoint: RouteWaypoint) {
        routeCreationRepository.addWaypoint(waypoint)
    }

    fun removeWaypoint(waypoint: RouteWaypoint) {
        routeCreationRepository.removeWaypoint(waypoint)
    }

    fun setName(name: String) {
        routeCreateState.value?.let { routeCreateState ->
            _routeCreateState.value = routeCreateState.copy(
                name = name
            )
        }
    }

    fun clearWaypoints() {
        routeCreationRepository.clearWaypoints()
    }

    suspend fun saveRoute() {
        val route = Route(
            name = routeCreateState.value?.name ?: "Route",
            createdTime = Date(),
            updatedTime = Date()
        )

        route?.let { route ->
            if (route.id == 0L) {
                val routeId = routeRepository.insert(route, routeCreationRepository.waypoints.value ?: emptyList())

            } else {
//                routeRepository.update(route)
            }
        }
        clearWaypoints()
    }
}