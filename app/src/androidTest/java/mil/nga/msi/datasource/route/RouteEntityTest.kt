package mil.nga.msi.datasource.route

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.UserDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date


@RunWith(AndroidJUnit4::class)
class RouteEntityTest {

    private lateinit var dao: RouteDao
    private lateinit var db: UserDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()
        dao = db.routeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun should_insert_route() = runTest {
        val insert = Route(Date(), "Cool Route", Date())
        val id = dao.insert(insert)

        val route = dao.getRoute(id)
        assertNotNull(route)
        assertRoutesEqual(id, insert, route!!)
    }

    @Test
    @Throws(Exception::class)
    fun should_insert_route_with_waypoints() = runTest {
        val insert = Route(Date(), "Cool Route", Date())
        val waypoint = RouteWaypoint(DataSource.ASAM, "2022-1")

        val id = dao.insert(insert, listOf(waypoint))

        val route = dao.getRouteWithWaypoints(id)
        assertNotNull(route)
        assertRoutesEqual(id, insert, route!!.route)
        val insertedWaypoints = route.waypoints
        assertNotNull(insertedWaypoints)
        assertEquals(1, insertedWaypoints.size)
        val insertedWaypoint = insertedWaypoints[0]
        assertNotNull(insertedWaypoint)
    }

    @Test
    fun should_update_route() = runTest {
        val insert = Route(Date(), "Cool Route", Date())
        val id = dao.insert(insert)

        val route = dao.getRoute(id)
        assertNotNull(route)
        route!!.name = "Even Cooler Route"
        dao.update(route)

        val updated = dao.getRoute(id)
        assertNotNull(updated)
        assertRoutesEqual(id, route, updated!!)
    }

    @Test
    fun should_remove_route() = runTest {
        val insert = Route(Date(), "Cool Route", Date())
        val id = dao.insert(insert)

        val route = dao.getRoute(id)
        dao.delete(route!!)

        val deleted = dao.getRoute(id)
        assertNull(deleted)
    }
}
