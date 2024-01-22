package mil.nga.msi.repository.route

import mil.nga.msi.datasource.route.Route
import mil.nga.msi.datasource.route.RouteWaypoint
import javax.inject.Inject

class RouteRepository @Inject constructor(
    private val localDataSource: RouteLocalDataSource
) {
    suspend fun insert(route: Route, waypoints: List<RouteWaypoint>) = localDataSource.insert(route, waypoints)
    suspend fun insert(route: Route) = localDataSource.insert(route)
    suspend fun getRoute(id: Long) = localDataSource.getRoute(id)
    suspend fun delete(route: Route) = localDataSource.delete(route)
    suspend fun update(route: Route) = localDataSource.update(route)
    suspend fun update(route: Route, waypoints: List<RouteWaypoint>) = localDataSource.update(route, waypoints)

    fun getRoutes(
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double
    ) = localDataSource.getRoutes(minLatitude, minLongitude, maxLatitude, maxLongitude)

    fun observeRoutes() = localDataSource.observeRoutes()
    fun observeRoutesWithWaypoints() = localDataSource.observeRoutesWithWaypoints()
    suspend fun getRouteWithWaypoints(routeId: Long) = localDataSource.getRouteWithWaypoints(routeId)
    fun observeRouteWithWaypoints(id: Long) = localDataSource.observeRouteWithWaypoints(id)

    fun observeRouteMapItems() = localDataSource.observeRouteMapItems()
}