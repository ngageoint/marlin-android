package mil.nga.msi.datasource.light

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertAsamsEqual
import assertLightsEqual
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
class LightEntityTest {

    private lateinit var dao: LightDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.lightDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val light = Light(
            id = "1",
            volumeNumber = "1",
            featureNumber = "1",
            characteristicNumber = 1,
            noticeWeek = "01",
            noticeYear = "01",
            latitude = 0.0,
            longitude = 0.0
        )

        dao.insert(light)
        val lights = dao.getLights()

        assertEquals(lights.size, 1)
        assertLightsEqual(lights.first(), light)
    }

    @Test
    @Throws(Exception::class)
    fun readById() = runTest {
        val light = Light(
            id = "1",
            volumeNumber = "1",
            featureNumber = "1",
            characteristicNumber = 1,
            noticeWeek = "01",
            noticeYear = "01",
            latitude = 0.0,
            longitude = 0.0
        )

        dao.insert(light)
        val read = dao.getLight(light.volumeNumber, light.featureNumber, light.characteristicNumber)

        assertNotNull(read)
        assertLightsEqual(light, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            Light(
                id = "1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "2",
                volumeNumber = "2",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "3",
                volumeNumber = "3",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        dao.insert(insert)
        val read = dao.getLights()

        assertEquals(read.size, 3)
        insert.forEachIndexed { index, _ -> assertLightsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = Light(
            id = "1",
            volumeNumber = "1",
            featureNumber = "1",
            characteristicNumber = 1,
            noticeWeek = "01",
            noticeYear = "01",
            latitude = 0.0,
            longitude = 0.0
        ).apply {
            internationalFeature = "1"
        }
        dao.insert(insert)
        insert.internationalFeature = "2"
        dao.update(insert)

        val read = dao.getLight(insert.volumeNumber, insert.featureNumber, insert.characteristicNumber)

        assertNotNull(read)
        assertEquals("2", read?.internationalFeature)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            Light(
                id = "1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "2",
                volumeNumber = "2",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "3",
                volumeNumber = "3",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(3, count)
    }

    @Test
    fun readMultipleByReference() = runTest {
        val insert = listOf(
            Light(
                id = "1--1--1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "2--2--2",
                volumeNumber = "2",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 0.0,
                longitude = 0.0
            ),
            Light(
                id = "3--3--3",
                volumeNumber = "3",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 0.0,
                longitude = 0.0
            )
        )

        dao.insert(insert)

        val read = dao.getLights(listOf(insert[0].compositeKey(), insert[1].compositeKey()))

        assertEquals(2, read.size)
        assertLightsEqual(insert[0], read[0])
        assertLightsEqual(insert[1], read[1])
    }

    @Test
    @Throws(Exception::class)
    fun latestLight() = runTest {
        val insert = listOf(
            Light(
                id = "1--1--1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 0.0,
                longitude = 0.0
            ).apply { noticeNumber = 1 },
            Light(
                id = "1--2--2",
                volumeNumber = "1",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 0.0,
                longitude = 0.0
            ).apply { noticeNumber = 2 },
            Light(
                id = "1--3--3",
                volumeNumber = "1",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 0.0,
                longitude = 0.0
            ).apply { noticeNumber = 3 },
        )

        dao.insert(insert)
        val read = dao.getLatestLight("1")

        assertNotNull(read)
        assertLightsEqual(insert[2], read!!)
    }

    @Test
    fun readBbox() = runTest {
        val insert = listOf(
            Light(
                id = "1--1--1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 10.0,
                longitude = 10.0
            ),
            Light(
                id = "2--2--2",
                volumeNumber = "2",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 20.0,
                longitude = 20.0
            ),
            Light(
                id = "3--3--3",
                volumeNumber = "3",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 30.0,
                longitude = 30.0
            )
        )

        dao.insert(insert)

        val read = dao.getLights(
            minLatitude = 5.0,
            minLongitude = 5.0,
            maxLatitude = 25.0,
            maxLongitude = 25.0
        )

        assertEquals(2, read.size)
        assertLightsEqual(insert[0], read[0])
        assertLightsEqual(insert[1], read[1])
    }


    @Test
    fun readBboxForCharacteristicNumber() = runTest {
        val insert = listOf(
            Light(
                id = "1--1--1",
                volumeNumber = "1",
                featureNumber = "1",
                characteristicNumber = 1,
                noticeWeek = "01",
                noticeYear = "01",
                latitude = 10.0,
                longitude = 10.0
            ),
            Light(
                id = "2--2--2",
                volumeNumber = "2",
                featureNumber = "2",
                characteristicNumber = 2,
                noticeWeek = "02",
                noticeYear = "02",
                latitude = 20.0,
                longitude = 20.0
            ),
            Light(
                id = "3--3--3",
                volumeNumber = "3",
                featureNumber = "3",
                characteristicNumber = 3,
                noticeWeek = "03",
                noticeYear = "03",
                latitude = 30.0,
                longitude = 30.0
            )
        )

        dao.insert(insert)

        val read = dao.getLights(
            minLatitude = 5.0,
            minLongitude = 5.0,
            maxLatitude = 25.0,
            maxLongitude = 25.0,
            characteristicNumber = 1
        )

        assertEquals(1, read.size)
        assertLightsEqual(insert[0], read[0])
    }
}
