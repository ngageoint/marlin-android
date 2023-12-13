package mil.nga.msi.ui.route.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.utils.sphericalDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.msi.location.LocationPolicy
import mil.nga.msi.repository.route.RouteCreationRepository
import mil.nga.msi.repository.route.RouteRepository
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RouteCreateViewModel @Inject constructor(
    val locationPolicy: LocationPolicy,
    private val routeRepository: RouteRepository,
    private val routeCreationRepository: RouteCreationRepository
): ViewModel() {

    var name = mutableStateOf("")

    val distance = routeCreationRepository.waypoints.map { waypoints ->
        var distance = 0.0
        var lastCoordinate: LatLng? = null
        waypoints.forEach { waypoint ->
            val (title, coordinate) = waypoint.getTitleAndCoordinate()
            if (lastCoordinate == null) {
                lastCoordinate = coordinate
            }

            lastCoordinate?.let { last ->
                coordinate?.let { current ->
                    distance += last.sphericalDistance(current)
                }
            }
        }

        distance
    }

    val waypoints = routeCreationRepository.waypoints

    val locationProvider = locationPolicy.bestLocationProvider

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun removeWaypoint(waypoint: RouteWaypoint) = routeCreationRepository.removeWaypoint(waypoint)

    fun setName(name: String) {
        this.name.value = name
    }

    private fun clearWaypoints() = routeCreationRepository.clearWaypoints()

    suspend fun saveRoute() {
        val route = Route(
            name = name.value,
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