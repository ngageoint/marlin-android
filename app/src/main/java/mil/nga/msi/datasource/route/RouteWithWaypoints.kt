package mil.nga.msi.datasource.route

import androidx.room.Embedded
import androidx.room.Relation
import java.util.Collections

data class RouteWithWaypoints(
    @Embedded val route: Route,
    @Relation(
      parentColumn = "id",
      entityColumn = "route_id"
    )
    val waypoints: List<RouteWaypoint>
) {

    fun getSortedWaypoints(): List<RouteWaypoint> {
        Collections.sort(waypoints)
        return waypoints
    }

}
