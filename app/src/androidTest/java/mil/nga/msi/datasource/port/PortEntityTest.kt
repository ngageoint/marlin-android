package mil.nga.msi.datasource.port

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertPortsEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PortEntityTest {

    private lateinit var dao: PortDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.portDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val port = Port(1, "1", 0.0, 0.0)
        dao.insert(port)
        val ports = dao.getPorts()

        assertEquals(ports.size, 1)
        assertPortsEqual(ports.first(), port)
    }

    @Test
    @Throws(Exception::class)
    fun readByReference() = runTest {
        val insert = Port(1, "1", 0.0, 0.0)
        dao.insert(insert)
        val read = dao.getPort(insert.portNumber)

        assertNotNull(read)
        assertPortsEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            Port(1, "1", 0.0, 0.0),
            Port(2, "2", 0.0, 0.0),
            Port(3, "3", 0.0, 0.0),
            Port(4, "4", 0.0, 0.0),
        )

        dao.insert(insert)
        val read = dao.getPorts()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertPortsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = Port(1, "1", 0.0, 0.0).apply {
            regionNumber = 1
        }
        dao.insert(insert)

        insert.regionNumber = 2
        dao.update(insert)

        val read = dao.getPort(insert.portNumber)

        assertNotNull(read)
        assertEquals(2, read?.regionNumber)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            Port(1, "1", 0.0, 0.0),
            Port(2, "2", 0.0, 0.0),
            Port(3, "3", 0.0, 0.0),
            Port(4, "4", 0.0, 0.0),
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readMultipleById() = runTest {
        val insert = listOf(
            Port(1, "1", 0.0, 0.0),
            Port(2, "2", 0.0, 0.0),
            Port(3, "3", 0.0, 0.0),
            Port(4, "4", 0.0, 0.0),
        )

        dao.insert(insert)
        val read = dao.getPorts(listOf(1, 2))

        assertEquals(read.size, 2)
        assertPortsEqual(insert[0], read[0])
        assertPortsEqual(insert[1], read[1])
    }

    @Test
    fun readBbox() = runTest {
        val insert = listOf(
            Port(1, "1", 10.0, 10.0),
            Port(2, "2", 20.0, 20.0),
            Port(3, "3", 3.0, 30.0),
        )

        dao.insert(insert)

        val read = dao.getPorts(
            minLatitude = 5.0,
            minLongitude = 5.0,
            maxLatitude = 25.0,
            maxLongitude = 25.0
        )

        assertEquals(2, read.size)
        assertPortsEqual(insert[0], read[0])
        assertPortsEqual(insert[1], read[1])
    }
}
