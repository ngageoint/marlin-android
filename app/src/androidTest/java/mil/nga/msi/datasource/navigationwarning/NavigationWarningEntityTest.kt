package mil.nga.msi.datasource.navigationwarning

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

@RunWith(AndroidJUnit4::class)
class NavigationWarningEntityTest {

    private lateinit var dao: NavigationalWarningDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.navigationalWarningDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val warning = NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
        dao.insert(warning)
        val warnings = dao.getNavigationalWarnings()

        assertEquals(warnings.size, 1)
        assertNavigationWarningsEqual(warnings.first(), warning)
    }

    @Test
    @Throws(Exception::class)
    fun readByReference() = runTest {
        val insert = NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
        dao.insert(insert)
        val read = dao.getNavigationalWarning(insert.number, insert.year, insert.navigationArea)

        assertNotNull(read)
        assertNavigationWarningsEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("4", 4, 2023, NavigationArea.HYDROARC, Date()),
        )

        dao.insert(insert)
        val read = dao.getNavigationalWarnings()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertNavigationWarningsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()).apply {
            text = "1"
        }
        dao.insert(insert)

        insert.text = "2"
        dao.update(insert)

        val read = dao.getNavigationalWarning(insert.number, insert.year, insert.navigationArea)

        assertNotNull(read)
        assertEquals("2", read?.text)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("4", 4, 2023, NavigationArea.HYDROARC, Date()),
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readMultipleByReference() = runTest {
        val insert = listOf(
            NavigationalWarning("1--2023--${NavigationArea.HYDROARC}", 1, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("2--2023--${NavigationArea.HYDROARC}", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3--2023--${NavigationArea.HYDROARC}", 3, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("4--2023--${NavigationArea.HYDROARC}", 4, 2023, NavigationArea.HYDROARC, Date()),
        )

        dao.insert(insert)
        val read = dao.getNavigationalWarnings(listOf(insert[0].id, insert[1].id))

        assertEquals(read.size, 2)
        assertNavigationWarningsEqual(insert[0], read[0])
        assertNavigationWarningsEqual(insert[1], read[1])
    }

    @Test
    fun readMultipleByArea() = runTest {
        val insert = listOf(
            NavigationalWarning("1--2023--${NavigationArea.HYDROARC}", 1, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("2--2023--${NavigationArea.HYDROARC}", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3--2023--${NavigationArea.HYDROARC}", 3, 2023, NavigationArea.HYDROLANT, Date()),
            NavigationalWarning("4--2023--${NavigationArea.HYDROARC}", 4, 2023, NavigationArea.HYDROLANT, Date()),
        )

        dao.insert(insert)
        val read = dao.getNavigationalWarningsByArea(NavigationArea.HYDROARC).first()

        assertEquals(read.size, 2)
        assertNavigationWarningsEqual(insert[0], read[0])
        assertNavigationWarningsEqual(insert[1], read[1])
    }

    @Test
    fun readGroup() = runTest {
        val insert = listOf(
            NavigationalWarning("1--2023--${NavigationArea.HYDROARC}", 1, 2023, NavigationArea.HYDROARC, Date()).apply {
                issueDate = Date.from(LocalDateTime.parse("2019-01-01T00:00:00").toInstant(ZoneOffset.UTC))
            },
            NavigationalWarning("2--2023--${NavigationArea.HYDROARC}", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3--2023--${NavigationArea.HYDROARC}", 3, 2023, NavigationArea.HYDROLANT, Date()),
            NavigationalWarning("4--2023--${NavigationArea.HYDROARC}", 4, 2023, NavigationArea.HYDROLANT, Date()),
        )

        dao.insert(insert)
        val read = dao.getNavigationalWarningsByNavigationArea(
            hydroarc = Date.from(LocalDateTime.parse("2020-01-01T00:00:00").toInstant(ZoneOffset.UTC)),
            hydrolant = Date.from(LocalDateTime.parse("2020-01-01T00:00:00").toInstant(ZoneOffset.UTC)),
            hydropac = Date(),
            navareaIV = Date(),
            navareaXII = Date(),
            special = Date()
        ).first()

        assertEquals(read.size, 2)
        assertEquals(2, read[0].total)
        assertEquals(1, read[0].unread)
        assertEquals(2, read[1].total)
        assertEquals(2, read[1].unread)
    }

    @Test
    fun readGreaterThan() = runTest {
        val insert = listOf(
            NavigationalWarning("1--2023--${NavigationArea.HYDROARC}", 1, 2023, NavigationArea.HYDROARC, Date()).apply {
                issueDate = Date.from(LocalDateTime.parse("2018-01-01T00:00:00").toInstant(ZoneOffset.UTC))
            },
            NavigationalWarning("2--2023--${NavigationArea.HYDROARC}", 2, 2023, NavigationArea.HYDROARC, Date()).apply {
                issueDate = Date.from(LocalDateTime.parse("2023-01-01T00:00:00").toInstant(ZoneOffset.UTC))
            },
            NavigationalWarning("3--2023--${NavigationArea.HYDROARC}", 3, 2023, NavigationArea.HYDROLANT, Date()),
            NavigationalWarning("4--2023--${NavigationArea.HYDROARC}", 4, 2023, NavigationArea.HYDROLANT, Date()),
        )

        dao.insert(insert)

        val read = dao.getNavigationalWarningsGreaterThan(
            date = Date.from(LocalDateTime.parse("2019-01-01T00:00:00").toInstant(ZoneOffset.UTC)),
            navigationArea = NavigationArea.HYDROARC
        ).first()

        assertEquals(read.size, 1)
        assertNavigationWarningsEqual(insert[1], read[0])
    }

    @Test
    fun delete() = runTest {
        val insert = listOf(
            NavigationalWarning("1--2023--${NavigationArea.HYDROARC}", 1, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("2--2023--${NavigationArea.HYDROARC}", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3--2023--${NavigationArea.HYDROARC}", 3, 2023, NavigationArea.HYDROLANT, Date()),
            NavigationalWarning("4--2023--${NavigationArea.HYDROARC}", 4, 2023, NavigationArea.HYDROLANT, Date()),
        )

        dao.insert(insert)
        dao.deleteNavigationalWarnings(listOf(1,2))
        val read = dao.getNavigationalWarnings()

        assertEquals(read.size, 2)
        assertNavigationWarningsEqual(insert[2], read[0])
        assertNavigationWarningsEqual(insert[3], read[1])
    }
}
