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

    fun observeRoutes() = localDataSource.observeRoutes()
    fun observeRoutesWithWaypoints() = localDataSource.observeRoutesWithWaypoints()
}