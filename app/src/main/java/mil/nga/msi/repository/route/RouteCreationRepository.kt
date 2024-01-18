package mil.nga.msi.repository.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.TileOverlayState
import com.google.maps.android.ktx.utils.sphericalDistance
import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import mil.nga.sf.LineString
import mil.nga.sf.Point
import mil.nga.sf.geojson.FeatureCollection
import mil.nga.sf.geojson.FeatureConverter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteCreationRepository @Inject constructor(
    private val routeRepository: RouteRepository
) {
    private var _route = MutableLiveData<Route>()
    val route : LiveData<Route> get() = _route

    var tileOverlayState: TileOverlayState? = null

    fun setRoute(route: Route) {
        _route.value = route
        tileOverlayState?.clearTileCache()
    }

    private val _waypoints = MutableLiveData<List<RouteWaypoint>>(emptyList())
    val waypoints: LiveData<List<RouteWaypoint>> = _waypoints

    fun addFirstWaypointIfEmpty(waypoint: RouteWaypoint) {
        val value = waypoints.value?.toMutableList() ?: mutableListOf()
        if (value.isEmpty()) {
            waypoint.order = value.size
            value.add(waypoint)
            _waypoints.value = value

            updateRoute()
        }
    }

    fun addWaypoint(waypoint: RouteWaypoint) {
        val value = waypoints.value?.toMutableList() ?: mutableListOf()
        waypoint.order = value.size
        value.add(waypoint)
        _waypoints.value = value

        updateRoute()
    }

    fun removeWaypoint(waypoint: RouteWaypoint) {
        val value = waypoints.value?.toMutableList() ?: mutableListOf()
        value.remove(waypoint)
        _waypoints.value = value
        updateRoute()
    }

    fun clearWaypoints() {
        _waypoints.value = emptyList()
        updateRoute()
    }

    fun moveWaypoint(fromIndex: Int, toIndex: Int): List<RouteWaypoint> {
        val list = waypoints.value?.toMutableList() ?: mutableListOf()
        list.apply { add(toIndex, removeAt(fromIndex)) }
        list.forEachIndexed { index, waypoint ->
            waypoint.order = index
        }
        _waypoints.value = list
        updateRoute()
        return list
    }

    private fun updateRoute() {
        var distance = 0.0
        var lastCoordinate: LatLng? = null

        var minLatitude: Double = 90.0
        var minLongitude: Double = 180.0
        var maxLatitude: Double = -90.0
        var maxLongitude: Double = -180.0

        val points = _waypoints.value?.mapNotNull { waypoint ->
            val (title, coordinate) = waypoint.getTitleAndCoordinate()
            coordinate?.let {
                minLatitude = minOf(minLatitude, coordinate.latitude)
                minLongitude = minOf(minLongitude, coordinate.longitude)
                maxLatitude = maxOf(maxLatitude, coordinate.latitude)
                maxLongitude = maxOf(maxLongitude, coordinate.longitude)
            }

            if (lastCoordinate == null) {
                lastCoordinate = coordinate
            }

            lastCoordinate?.let { last ->
                coordinate?.let { current ->
                    distance += last.sphericalDistance(current)
                }
            }

            coordinate?.let {
                Point(coordinate.longitude, coordinate.latitude)
            }
        }

        if (points != null && points.size > 1) {
            var fc = FeatureCollection()
            fc.addFeature(FeatureConverter.toFeature(LineString(points)))
            _route.value?.geoJson = FeatureConverter.toStringValue(fc)
        } else {
            _route.value?.geoJson = null
        }
        _route.value?.distanceMeters = distance
        _route.value?.minLatitude = minLatitude
        _route.value?.maxLatitude = maxLatitude
        _route.value?.minLongitude = minLongitude
        _route.value?.maxLongitude = maxLongitude

        tileOverlayState?.clearTileCache()
    }

    suspend fun saveRoute() {
        route.value?.let { route ->
            if (route.id == 0L) {
                val routeId = routeRepository.insert(
                    route,
                    waypoints.value ?: emptyList()
                )

            } else {
//                routeRepository.update(route)
            }
        }
        clearWaypoints()
    }
}