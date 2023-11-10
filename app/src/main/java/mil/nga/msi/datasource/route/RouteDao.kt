package mil.nga.msi.datasource.route

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Transaction
    @Query("SELECT * FROM routes ORDER BY createdTime DESC")
    fun observeRoutesWithWaypoints(): Flow<List<RouteWithWaypoints>>

    @Query("SELECT * from routes ORDER BY createdTime DESC")
    fun observeRoutes(): Flow<List<Route>>

    @Query("SELECT * from routes WHERE id = :id")
    suspend fun getRoute(id: Long): Route?

    @Transaction
    @Query("SELECT * from routes WHERE id = :id")
    suspend fun getRouteWithWaypoints(id: Long): RouteWithWaypoints?

    @Transaction
    suspend fun insert(route: Route, waypoints: List<RouteWaypoint>): Long {
        val routeId: Long = insert(route)

        // Set routeId for all related routeWaypoints
        for (waypointEntity in waypoints) {
            waypointEntity.routeId = routeId
            insertWaypoint(waypointEntity)
        }
        return routeId
    }

    @Insert
    suspend fun insert(route: Route): Long

    @Insert
    suspend fun insertWaypoint(waypoint: RouteWaypoint)

    @Delete
    suspend fun delete(route: Route)

    @Delete
    suspend fun deleteWaypoint(waypoint: RouteWaypoint)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(route: Route): Int
}