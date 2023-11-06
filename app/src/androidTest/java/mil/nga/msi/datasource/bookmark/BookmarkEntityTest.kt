package mil.nga.msi.datasource.bookmark

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.UserDatabase
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class BookmarkEntityTest {

    private lateinit var dao: BookmarkDao
    private lateinit var db: UserDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).build()
        dao = db.bookmarkDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun should_insert_bookmark() = runTest {
        val insert = Bookmark("1", DataSource.ASAM, Date(), "notes")
        dao.insert(insert)
        val bookmark = dao.getBookmark(DataSource.ASAM, "1")

        assertNotNull(bookmark)
        assertBookmarksEqual(insert, bookmark!!)
    }


    @Test
    fun should_update_bookmark() = runTest {
        val insert = Bookmark("1", DataSource.ASAM, Date())
        dao.insert(insert)

        insert.notes = "notes"
        dao.update(insert)

        val bookmark = dao.getBookmark(DataSource.ASAM, "1")

        assertNotNull(bookmark)
        assertBookmarksEqual(insert, bookmark!!)
    }

    @Test
    fun should_remove_bookmark() = runTest {
        val insert = Bookmark("1", DataSource.ASAM, Date())
        dao.insert(insert)
        dao.delete(insert)
        val bookmark = dao.getBookmark(DataSource.ASAM, "1")
        assertNull(bookmark)
    }
}
