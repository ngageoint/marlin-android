package mil.nga.msi.datasource.dgpsstation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertAsamsEqual
import assertDgpsStationsEqual
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
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DgpsStationEntityTest {

    private lateinit var dao: DgpsStationDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.dgpsStationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val station = DgpsStation(
            id = "1",
            volumeNumber = "1.0",
            featureNumber = 1f,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
        )
        dao.insert(station)
        val stations = dao.getDgpsStations()

        assertEquals(stations.size, 1)
        assertDgpsStationsEqual(stations.first(), station)
    }

    @Test
    @Throws(Exception::class)
    fun readByReference() = runTest {
        val insert = DgpsStation(
            id = "1",
            volumeNumber = "1.0",
            featureNumber = 1f,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
        )

        dao.insert(insert)
        val read = dao.getDgpsStation(insert.volumeNumber, insert.featureNumber)

        assertNotNull(read)
        assertDgpsStationsEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            DgpsStation(
                id = "1",
                volumeNumber = "1.0",
                featureNumber = 1f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 1.0,
                longitude = 1.0
            ),
            DgpsStation(
                id = "2",
                volumeNumber = "2.0",
                featureNumber = 2f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 2.0,
                longitude = 2.0
            ),
            DgpsStation(
                id = "3",
                volumeNumber = "3.0",
                featureNumber = 3f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 3.0,
                longitude = 3.0
            )
        )

        dao.insert(insert)
        val read = dao.getDgpsStations()

        assertEquals(3, read.size)
        insert.forEachIndexed { index, _ -> assertDgpsStationsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = DgpsStation(
            id = "1",
            volumeNumber = "1.0",
            featureNumber = 1f,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
        ).apply {
            aidType = "1"
        }
        dao.insert(insert)

        insert.aidType = "2"
        dao.update(insert)

        val read = dao.getDgpsStation(insert.volumeNumber, insert.featureNumber)

        assertNotNull(read)
        assertEquals("2", read?.aidType)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            DgpsStation(
                id = "1",
                volumeNumber = "1.0",
                featureNumber = 1f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 1.0,
                longitude = 1.0
            ),
            DgpsStation(
                id = "2",
                volumeNumber = "2.0",
                featureNumber = 2f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 2.0,
                longitude = 2.0
            ),
            DgpsStation(
                id = "3",
                volumeNumber = "3.0",
                featureNumber = 3f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 3.0,
                longitude = 3.0
            )
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(3, count)
    }

    @Test
    fun readMultipleByReference() = runTest {
        val insert = listOf(
            DgpsStation(
                id = "1",
                volumeNumber = "1.0",
                featureNumber = 1f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 1.0,
                longitude = 1.0
            ),
            DgpsStation(
                id = "2",
                volumeNumber = "2.0",
                featureNumber = 2f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 2.0,
                longitude = 2.0
            ),
            DgpsStation(
                id = "3",
                volumeNumber = "3.0",
                featureNumber = 3f,
                noticeWeek = "01",
                noticeYear = "23",
                latitude = 3.0,
                longitude = 3.0
            )
        )

        dao.insert(insert)
        val read = dao.getDgpsStations(listOf("1", "2"))

        assertEquals(read.size, 2)
        assertDgpsStationsEqual(insert[0], read[0])
        assertDgpsStationsEqual(insert[1], read[1])
    }
}
