package mil.nga.msi.datasource.electronicpublication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertEPubsEqual
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.MsiDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ElectronicPublicationEntityTest {

    private lateinit var dao: ElectronicPublicationDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.electronicPublicationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert_and_read() = runTest {
        val epub = ElectronicPublication("test.epub.1")
        dao.insert(epub)
        val epubs = dao.readElectronicPublications()

        assertEquals(epubs.size, 1)
        assertEPubsEqual(epubs.first(), epub)
    }

    @Test
    fun insert_and_read_multiple() = runTest {
        val inserted = listOf(
            ElectronicPublication("test.epub.1", fileSize = 10000),
            ElectronicPublication("test.epub.2", fileSize = 20000),
            ElectronicPublication("test.epub.3", fileSize = 30000)
        )
        dao.insert(inserted)
        val read = dao.readElectronicPublications()

        assertEquals(read.size, 3)
        inserted.forEachIndexed { pos, epub -> assertEPubsEqual(read[pos], inserted[pos]) }
    }
}
