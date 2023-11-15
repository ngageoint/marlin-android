package mil.nga.msi.ui.route.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.route.RouteRepository
import javax.inject.Inject

data class RouteCreateState(
    val name: String? = null,
    val route: Route? = null,
    val waypoints: List<RouteWaypoint>
)
@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    val locationPolicy: LocationPolicy,
    private val routeRepository: RouteRepository
): ViewModel() {

    private val _routeCreateState = MutableLiveData<RouteCreateState?>()
    val routeCreateState: LiveData<RouteCreateState?> = _routeCreateState

    val locationProvider = locationPolicy.bestLocationProvider

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun addWaypoint(waypoint: RouteWaypoint) {
        routeCreateState.value?.let { routeCreateState ->
            val waypoints = routeCreateState.waypoints.toMutableSet().apply {
                add(waypoint)
            }.toList()
            _routeCreateState.value = routeCreateState.copy(
                waypoints = waypoints
            )
        }
    }

    fun setName(name: String) {
        routeCreateState.value?.let { routeCreateState ->
            _routeCreateState.value = routeCreateState.copy(
                name = name
            )
        }
    }

    suspend fun saveRoute(route: Route?) {
        route?.let { route ->
            if (route.id == 0L) {
                val routeId = routeRepository.insert(route)
            } else {
//                routeRepository.update(route)
            }
        }
    }
}