package mil.nga.msi.datasource.bookmark

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamDao
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.dgpsstation.DgpsStationDao
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.ModuDao
import mil.nga.msi.repository.bookmark.BookmarkKey
import mil.nga.msi.repository.bookmark.BookmarkLocalDataSource
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkDataSourceTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_combine_all_bookmark_flows() = runTest {
      val asamDao = mockk<AsamDao>()
      coEvery { asamDao.observeBookmarkedAsams() } answers {
         flowOf(
            listOf(Asam("1", Date(), 0.0, 0.0))
         )
      }

      val dgpsStationDao = mockk<DgpsStationDao>()
      coEvery { dgpsStationDao.observeBookmarkedDgpsStations() } answers {
         flowOf(
            listOf(
               DgpsStation(
                  id = "1",
                  volumeNumber = "1",
                  featureNumber = 1f,
                  noticeWeek = "01",
                  noticeYear = "23",
                  latitude = 1.0,
                  longitude = 1.0
               )
            )
         )
      }

      val moduDao = mockk<ModuDao>()
      coEvery { moduDao.observeBookmarkedModus() } answers {
         flowOf(
            listOf(Modu("1", Date(), 0.0, 0.0))
         )
      }

      val dataSource = BookmarkLocalDataSource(
         asamDao = asamDao,
         moduDao = moduDao,
         dgpsStationDao = dgpsStationDao
      )

      dataSource.observeBookmarks().first()

      coVerify {
         asamDao.observeBookmarkedAsams()
         dgpsStationDao.observeBookmarkedDgpsStations()
         moduDao.observeBookmarkedModus()
      }
   }

   @Test
   fun should_combine_and_sort_bookmark_flow() = runTest {
      val dateFormat = SimpleDateFormat("yyyy-MM-dd")

      val asamDao = mockk<AsamDao>()
      coEvery { asamDao.observeBookmarkedAsams() } answers {
         flowOf(
            listOf(
               Asam("1", Date(), 0.0, 0.0).apply { bookmarkDate =  dateFormat.parse("2020-01-01")!!},
               Asam("2", Date(), 0.0, 0.0).apply { bookmarkDate =  dateFormat.parse("2023-01-01")!!},
            )
         )
      }

      val moduDao = mockk<ModuDao>()
      coEvery { moduDao.observeBookmarkedModus() } answers {
         flowOf(
            listOf(
               Modu("3", Date(), 0.0, 0.0).apply { bookmarkDate =  dateFormat.parse("2022-01-01")!!},
               Modu("4", Date(), 0.0, 0.0).apply { bookmarkDate =  dateFormat.parse("2021-01-01")!!},
            )
         )
      }

      val dataSource = BookmarkLocalDataSource(
         asamDao = asamDao,
         moduDao = moduDao,
         dgpsStationDao = mockk()
      )

      val bookmarks = dataSource.observeBookmarks().first()
      assertEquals(4, bookmarks.size)
      assertEquals(dateFormat.parse("2023-01-01")!!, bookmarks[0].bookmarkDate)
      assertEquals(dateFormat.parse("2022-01-01")!!, bookmarks[1].bookmarkDate)
      assertEquals(dateFormat.parse("2021-01-01")!!, bookmarks[2].bookmarkDate)
      assertEquals(dateFormat.parse("2020-01-01")!!, bookmarks[3].bookmarkDate)
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
         asamDao = asamDao,
         moduDao = mockk(),
         dgpsStationDao = mockk()
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
         asamDao = asamDao,
         moduDao = mockk(),
         dgpsStationDao = mockk()
      )

      dataSource.setBookmark(bookmark, bookmarked)

      coVerify {
         asamDao.setBookmark(reference, bookmarked, null, null)
      }
   }

   @Test
   fun should_bookmark_modu() = runTest {
      val name = "1"
      val notes = "notes"
      val bookmarked = true
      val bookmark = BookmarkKey.fromModu(Modu(name, Date(), 0.0, 0.0))

      val moduDao = mockk<ModuDao>()
      coEvery { moduDao.setBookmark(any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = mockk(),
         moduDao = moduDao,
         dgpsStationDao = mockk()
      )

      dataSource.setBookmark(bookmark, bookmarked, notes)

      coVerify {
         moduDao.setBookmark(name, bookmarked, any(), notes)
      }
   }

   @Test
   fun should_unbookmark_modu() = runTest {
      val name = "1"
      val bookmarked = false
      val bookmark = BookmarkKey.fromModu(Modu(name, Date(), 0.0, 0.0))

      val moduDao = mockk<ModuDao>()
      coEvery { moduDao.setBookmark(any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = mockk(),
         moduDao = moduDao,
         dgpsStationDao = mockk()
      )

      dataSource.setBookmark(bookmark, bookmarked)

      coVerify {
         moduDao.setBookmark(name, bookmarked, null, null)
      }
   }

   @Test
   fun should_bookmark_dgps_station() = runTest {
      val volumeNumber = "1"
      val featureNumber = 1f
      val notes = "notes"
      val bookmarked = true
      val bookmark = BookmarkKey.fromDgpsStation(
         DgpsStation(
            id = "1",
            volumeNumber = volumeNumber,
            featureNumber = featureNumber,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
         )
      )

      val dgpsStationDao = mockk<DgpsStationDao>()
      coEvery { dgpsStationDao.setBookmark(any(), any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = mockk(),
         moduDao = mockk(),
         dgpsStationDao = dgpsStationDao
      )

      dataSource.setBookmark(bookmark, bookmarked, notes)

      coVerify {
         dgpsStationDao.setBookmark(volumeNumber, featureNumber, bookmarked, any(), notes)
      }
   }

   @Test
   fun should_unbookmark_dgps_station() = runTest {
      val volumeNumber = "1"
      val featureNumber = 1f
      val bookmarked = false
      val bookmark = BookmarkKey.fromDgpsStation(
         DgpsStation(
            id = "1",
            volumeNumber = volumeNumber,
            featureNumber = featureNumber,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
         )
      )

      val dgpsStationDao = mockk<DgpsStationDao>()
      coEvery { dgpsStationDao.setBookmark(any(), any(), any(), any(), any()) } returns Unit

      val dataSource = BookmarkLocalDataSource(
         asamDao = mockk(),
         moduDao = mockk(),
         dgpsStationDao = dgpsStationDao
      )

      dataSource.setBookmark(bookmark, bookmarked)

      coVerify {
         dgpsStationDao.setBookmark(volumeNumber, featureNumber, bookmarked, null, null)
      }
   }
}