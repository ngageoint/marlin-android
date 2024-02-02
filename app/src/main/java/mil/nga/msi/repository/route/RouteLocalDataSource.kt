package mil.nga.msi.repository.route

import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteDao
import mil.nga.msi.datasource.route.RouteWaypoint
import javax.inject.Inject

class RouteLocalDataSource @Inject constructor(
    private val dao: RouteDao
) {
    fun observeRoutesWithWaypoints() = dao.observeRoutesWithWaypoints()
    fun observeRoutes() = dao.observeRoutes()
    fun observeRouteMapItems() = dao.observeRouteMapItems()

    fun getRoutes(
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double
    ) = dao.getRoutes(minLatitude, minLongitude, maxLatitude, maxLongitude)

    suspend fun getRoute(id: Long) = dao.getRoute(id)
    suspend fun getRouteWithWaypoints(id: Long) = dao.getRouteWithWaypoints(id)
    fun observeRouteWithWaypoints(id: Long) = dao.observeRouteWithWaypoints(id)

    suspend fun insert(route: Route, waypoints: List<RouteWaypoint>) = dao.insert(route, waypoints)
    suspend fun insert(route: Route) = dao.insert(route)

    suspend fun delete(route: Route) = dao.delete(route)

    suspend fun update(route: Route) = dao.update(route)
    suspend fun update(route: Route, waypoints: List<RouteWaypoint>) = dao.update(route, waypoints)
}