package mil.nga.msi.datasource.modu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertModusEqual
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
class ModuEntityTest {

    private lateinit var dao: ModuDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.moduDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val modu = Modu("1", Date(), 0.0, 0.0)
        dao.insert(modu)
        val modus = dao.getModus()

        assertEquals(modus.size, 1)
        assertModusEqual(modus.first(), modu)
    }

    @Test
    @Throws(Exception::class)
    fun readByReference() = runTest {
        val insert = Modu("1", Date(), 0.0, 0.0)
        dao.insert(insert)
        val read = dao.getModu(insert.name)

        assertNotNull(read)
        assertModusEqual(insert, read!!)
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            Modu("1", Date(), 1.0, 1.0),
            Modu("2", Date(), 2.0, 2.0),
            Modu("3", Date(), 3.0, 3.0),
            Modu("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val read = dao.getModus()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertModusEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = Modu("1", Date(), 0.0, 0.0).apply {
            position = "1"
        }
        dao.insert(insert)

        insert.position = "2"
        dao.update(insert)
        val read = dao.getModu(insert.name)

        assertNotNull(read)
        assertEquals("2", read?.position)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            Modu("1", Date(), 1.0, 1.0),
            Modu("2", Date(), 2.0, 2.0),
            Modu("3", Date(), 3.0, 3.0),
            Modu("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readMultipleByReference() = runTest {
        val insert = listOf(
            Modu("1", Date(), 1.0, 1.0),
            Modu("2", Date(), 2.0, 2.0),
            Modu("3", Date(), 3.0, 3.0),
            Modu("4", Date(), 4.0, 4.0)
        )

        dao.insert(insert)
        val read = dao.getModus(listOf("1", "2"))

        assertEquals(read.size, 2)
        assertModusEqual(insert[0], read[0])
        assertModusEqual(insert[1], read[1])
    }
}
