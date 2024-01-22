package mil.nga.msi.datasource.route

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Transaction
    @Query("SELECT * FROM routes ORDER BY createdTime DESC")
    fun observeRoutesWithWaypoints(): Flow<List<RouteWithWaypoints>>

    @Query("SELECT * from routes ORDER BY createdTime DESC")
    fun observeRoutes(): Flow<List<Route>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM routes WHERE geoJson IS NOT NULL")
    fun observeRouteMapItems(): Flow<List<RouteMapItem>>

    @RawQuery(observedEntities = [Route::class])
    @RewriteQueriesToDropUnusedColumns
    fun getRoutes(query: SupportSQLiteQuery): List<Route>

    @Query("SELECT * FROM routes WHERE (minLongitude <= :maxLongitude AND maxLongitude >= :minLongitude AND minLatitude <= :maxLatitude AND maxLatitude >= :minLatitude) OR maxLongitude < minLongitude")
    fun getRoutes(
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double
    ): List<Route>

    @Query("SELECT * from routes WHERE id = :id")
    suspend fun getRoute(id: Long): Route?

    @Transaction
    @Query("SELECT * from routes WHERE id = :id")
    suspend fun getRouteWithWaypoints(id: Long): RouteWithWaypoints?

    @Transaction
    @Query("SELECT * FROM routes WHERE id = :id")
    fun observeRouteWithWaypoints(id: Long): Flow<RouteWithWaypoints>

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

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(route: Route, waypoints: List<RouteWaypoint>): Int {
        getRouteWithWaypoints(route.id)?.let { routeWithWaypoints ->
            for (waypoint in routeWithWaypoints.waypoints) {
                deleteWaypoint(waypoint)
            }
        }

        val updates: Int = update(route)
        // Set routeId for all related routeWaypoints
        for (waypointEntity in waypoints) {
            waypointEntity.routeId = route.id
            insertWaypoint(waypointEntity)
        }
        return updates
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