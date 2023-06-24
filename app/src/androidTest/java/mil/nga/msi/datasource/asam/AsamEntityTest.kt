package mil.nga.msi.datasource.asam

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertAsamsEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AsamEntityTest {

    private lateinit var dao: AsamDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.asamDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val asam = Asam("1", Date(), 0.0, 0.0)
        dao.insert(asam)
        val asams = dao.getAsams()

        assertEquals(asams.size, 1)
        assertAsamsEqual(asams.first(), asam)
    }

    @Test
    @Throws(Exception::class)
    fun readByReference() = runTest {
        val insert = Asam("1", Date(), 0.0, 0.0)
        dao.insert(insert)
        val read = dao.getAsam(insert.reference)

        assertNotNull(read)
        assertAsamsEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            Asam("1", Date(), 1.0, 1.0),
            Asam("2", Date(), 2.0, 2.0),
            Asam("3", Date(), 3.0, 3.0),
            Asam("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val read = dao.getAsams()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertAsamsEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = Asam("1", Date(), 0.0, 0.0).apply {
            position = "1"
        }
        dao.insert(insert)

        insert.position = "2"
        dao.update(insert)

        val read = dao.getAsam(insert.reference)

        assertNotNull(read)
        assertEquals("2", read?.position)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            Asam("1", Date(), 1.0, 1.0),
            Asam("2", Date(), 2.0, 2.0),
            Asam("3", Date(), 3.0, 3.0),
            Asam("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readMultipleByReference() = runTest {
        val insert = listOf(
            Asam("1", Date(), 1.0, 1.0),
            Asam("2", Date(), 2.0, 2.0),
            Asam("3", Date(), 3.0, 3.0),
            Asam("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val read = dao.getAsams(listOf("1", "2"))

        assertEquals(read.size, 2)
        assertAsamsEqual(insert[0], read[0])
        assertAsamsEqual(insert[1], read[1])
    }

    @Test
    fun should_add_bookmark() = runTest {
        val reference = "1"
        dao.insert(listOf(Asam(reference, Date(), 1.0, 1.0)))

        val date = Date()
        val notes = "notes"
        dao.setBookmark(reference, true, date, notes)
        val asam = dao.getAsam(reference)

        assertEquals(true, asam?.bookmarked)
        assertEquals(notes, asam?.bookmarkNotes)
        assertEquals(date, asam?.bookmarkDate)
        assertEquals(reference, asam?.bookmarkId)
    }

    @Test
    fun should_remove_bookmark() = runTest {
        val reference = "1"
        dao.insert(
            listOf(
                Asam(reference, Date(), 1.0, 1.0).apply {
                    bookmarked = true
                    bookmarkNotes = "notes"
                    bookmarkDate = Date()
                }
            )
        )

        dao.setBookmark(reference, false)
        val asam = dao.getAsam(reference)

        assertEquals(false, asam?.bookmarked)
        assertNull(asam?.bookmarkNotes)
        assertNull(asam?.bookmarkDate)
    }
}
