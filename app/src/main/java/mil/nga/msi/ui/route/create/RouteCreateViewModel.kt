package mil.nga.msi.ui.route.create

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

//    var distance = mutableDoubleStateOf(0.0)
//    val distance = _distance.asDoubleState()

//    val _distance = MutableLiveData(0.0)
//    val distance: LiveData<Double> = _distance
//    fun setDistance(distance: Double) {
//        _distance.postValue(distance)
//    }

    private val _distance = MutableStateFlow(0.0)
    val distance: StateFlow<Double> = _distance
    fun setDistance(distance: Double) {
        _distance.value = distance
    }

    val waypoints = routeCreationRepository.waypoints

    val locationProvider = locationPolicy.bestLocationProvider

    fun setLocationEnabled(enabled: Boolean) {
        if (enabled) {
            locationPolicy.requestLocationUpdates()
        }
    }

    fun addWaypoint(waypoint: RouteWaypoint) {
        var currentDistance = 5.0
        routeCreationRepository.addWaypoint(waypoint)
        routeCreationRepository.waypoints.value?.let {
            var lastCoordinate: LatLng? = null
            for (waypoint in it) {
                val (title, coordinate) = waypoint.getTitleAndCoordinate()
                if (lastCoordinate == null) {
                    lastCoordinate = coordinate
                }
                lastCoordinate?.let {
                    coordinate?.let {
                        // nothing for right now
//                        currentDistance += lastCoordinate.sphericalDistance(coordinate)
                    }
                }
            }
        }
        Log.d("Dan", "setting distance to ${currentDistance}")
        setDistance(currentDistance)
    }

    fun removeWaypoint(waypoint: RouteWaypoint) {
        routeCreationRepository.removeWaypoint(waypoint)
    }

    fun setName(name: String) {
        this.name.value = name
    }

    private fun clearWaypoints() {
        routeCreationRepository.clearWaypoints()
    }

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