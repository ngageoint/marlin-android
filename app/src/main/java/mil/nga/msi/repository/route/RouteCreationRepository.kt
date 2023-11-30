package mil.nga.msi.repository.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mil.nga.msi.datasource.route.RouteWaypoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteCreationRepository @Inject constructor(
) {
    private val _waypoints = MutableLiveData<List<RouteWaypoint>>()
    val waypoints: LiveData<List<RouteWaypoint>> = _waypoints

    fun addWaypoint(waypoint: RouteWaypoint) {
        val value = waypoints.value?.toMutableList() ?: mutableListOf()
        value.add(waypoint)
        _waypoints.value = value
    }

    fun removeWaypoint(waypoint: RouteWaypoint) {
        val value = waypoints.value?.toMutableList() ?: mutableListOf()
        value.remove(waypoint)
        _waypoints.value = value
    }

    fun clearWaypoints() {
        _waypoints.value = emptyList()
    }
}