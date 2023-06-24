package mil.nga.msi.datasource.bookmark

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.testing.asPagingSourceFactory
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.bookmark.BookmarkLocalDataSource
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkDataSourceTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_bookmark_asam() = runTest {
      val reference = "1"
      val notes = "notes"
      val bookmarked = true
      val bookmark = BookmarkKey.fromAsam(Asam(reference, Date(), 0.0, 0.0))

      val asamDao = mockk<AsamDao>()
      coEvery { asamDao.setBookmark(any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = asamDao
      )

      dataSource.setBookmark(bookmark, bookmarked, notes)

      coVerify {
         asamDao.setBookmark(reference, bookmarked, any(), notes)
      }
   }

   @Test
   fun should_unbookmark_asam() = runTest {
      val reference = "1"
      val bookmarked = false
      val bookmark = BookmarkKey.fromAsam(Asam(reference, Date(), 0.0, 0.0))

      val asamDao = mockk<AsamDao>()
      coEvery { asamDao.setBookmark(any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = asamDao
      )

      dataSource.setBookmark(bookmark, bookmarked)

      coVerify {
         asamDao.setBookmark(reference, bookmarked, null, null)
      }
   }
}