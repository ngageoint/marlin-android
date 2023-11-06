package mil.nga.msi.datasource.radiobeacon

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RadioBeaconEntityTest {

    private lateinit var dao: RadioBeaconDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.radioBeaconDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val beacon = RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0)
        dao.insert(beacon)
        val beacons = dao.getRadioBeacons()

        assertEquals(beacons.size, 1)
        assertRadioBeaconsEqual(beacons.first(), beacon)
    }

    @Test
    @Throws(Exception::class)
    fun readById() = runTest {
        val insert = RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0)
        dao.insert(insert)
        val read = dao.getRadioBeacon(insert.volumeNumber, insert.featureNumber)

        assertNotNull(read)
        assertRadioBeaconsEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
            RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
            RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0),
            RadioBeacon("4", "4", "4", "01", "2023", 0.0, 0.0),
        )

        dao.insert(insert)
        val read = dao.getRadioBeacons()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertRadioBeaconsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0).apply {
            aidType = "1"
        }
        dao.insert(insert)

        insert.aidType = "2"
        dao.update(insert)

        val read = dao.getRadioBeacon(insert.volumeNumber, insert.featureNumber)

        assertNotNull(read)
        assertEquals("2", read?.aidType)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
            RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
            RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0),
            RadioBeacon("4", "4", "4", "01", "2023", 0.0, 0.0),
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readMultipleById() = runTest {
        val insert = listOf(
            RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
            RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
            RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0),
            RadioBeacon("4", "4", "4", "01", "2023", 0.0, 0.0),
        )

        dao.insert(insert)
        val read = dao.getRadioBeacons(listOf("1", "2"))

        assertEquals(read.size, 2)
        assertRadioBeaconsEqual(insert[0], read[0])
        assertRadioBeaconsEqual(insert[1], read[1])
    }
}
