package mil.nga.msi.datasource.route

import androidx.room.Embedded
import androidx.room.Relation

data class RouteWithWaypoints(
    @Embedded val route: Route,
    @Relation(
      parentColumn = "id",
      entityColumn = "route_id"
    )
    val waypoints: List<RouteWaypoint>
) {
}
