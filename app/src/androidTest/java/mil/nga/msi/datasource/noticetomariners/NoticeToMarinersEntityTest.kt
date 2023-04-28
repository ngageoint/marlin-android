package mil.nga.msi.datasource.noticetomariners

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertAsamsEqual
import assertNoticeToMarinersEqual
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
class NoticeToMarinersEntityTest {

    private lateinit var dao: NoticeToMarinersDao
    private lateinit var db: MsiDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MsiDatabase::class.java).build()
        dao = db.noticeToMarinersDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runTest {
        val notice = NoticeToMariners(1, "1", 1, "filename")
        dao.insert(notice)
        val notices = dao.getNoticeToMariners()

        assertEquals(notices.size, 1)
        assertNoticeToMarinersEqual(notices.first(), notice)
    }

    @Test
    @Throws(Exception::class)
    fun readByNoticeNumber() = runTest {
        val insert = listOf(
            NoticeToMariners(1, "1", 1, "filename"),
            NoticeToMariners(2, "2", 1, "filename"),
            NoticeToMariners(3, "3", 2, "filename"),
            NoticeToMariners(4, "4", 2, "filename")
        )

        dao.insert(insert)
        val read = dao.getNoticeToMariners(1)

        assertEquals(2, read.size)
        assertNoticeToMarinersEqual(insert[0], read[0])
        assertNoticeToMarinersEqual(insert[1], read[1])
    }

    @Test
    fun readMultiple() = runTest {
        val insert = listOf(
            NoticeToMariners(1, "1", 1, "filename"),
            NoticeToMariners(2, "2", 1, "filename"),
            NoticeToMariners(3, "3", 2, "filename"),
            NoticeToMariners(4, "4", 2, "filename")
        )

        dao.insert(insert)
        val read = dao.getNoticeToMariners()

        assertEquals(read.size, 4)
        insert.forEachIndexed { index, _ -> assertNoticeToMarinersEqual(read[index], insert[index]) }
    }

    @Test
    fun update() = runTest {
        val insert = NoticeToMariners(1, "1", 1, "filename").apply {
            odsContentId = "1"
        }
        dao.insert(insert)

        insert.odsContentId = "2"
        dao.update(insert)

        val read = dao.getNoticeToMariners(insert.noticeNumber)

        assertEquals(read.size, 1)
        assertEquals("2", read[0].odsContentId)
    }

    @Test
    fun count() = runTest {
        val insert = listOf(
            NoticeToMariners(1, "1", 1, "filename"),
            NoticeToMariners(2, "2", 1, "filename"),
            NoticeToMariners(3, "3", 2, "filename"),
            NoticeToMariners(4, "4", 2, "filename")
        )

        dao.insert(insert)
        val count = dao.count()

        assertEquals(4, count)
    }

    @Test
    fun readLatest() = runTest {
        val insert = listOf(
            NoticeToMariners(1, "1", 1, "filename"),
            NoticeToMariners(2, "2", 1, "filename"),
            NoticeToMariners(3, "3", 2, "filename"),
            NoticeToMariners(4, "4", 2, "filename")
        )

        dao.insert(insert)
        val read = dao.getNoticeToMariners(listOf(1, 2))

        assertEquals(read.size, 2)
        assertNoticeToMarinersEqual(insert[0], read[0])
        assertNoticeToMarinersEqual(insert[1], read[1])
    }
}
