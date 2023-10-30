package mil.nga.msi.work.noticetomariners

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersLocalDataSource
import mil.nga.msi.repository.noticetomariners.NoticeToMarinersRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class LoadNoticeToMarinersWorkerFactory(private val dataSource: NoticeToMarinersLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return LoadNoticeToMarinersWorker(appContext, workerParameters, dataSource)
   }
}

class RefreshNoticeToMarinersWorkerFactory(
   private val repository: NoticeToMarinersRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshNoticeToMarinersWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class NoticeToMarinersWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_notice_to_mariners() {
      val mockDataSource = mockk<NoticeToMarinersLocalDataSource>()
      every { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns emptyList()

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadNoticeToMarinersWorker>(context)
         .setWorkerFactory(LoadNoticeToMarinersWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_notice_to_mariners() {
      val mockDataSource = mockk<NoticeToMarinersLocalDataSource>()
      every { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadNoticeToMarinersWorker>(context)
         .setWorkerFactory(LoadNoticeToMarinersWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_notice_to_mariners_if_never_fetched() {
      val mockNoticeToMarinersRepository = mockk<NoticeToMarinersRepository>()
      coEvery { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NOTICE_TO_MARINERS) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNoticeToMarinersWorkerFactory(
         repository = mockNoticeToMarinersRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNoticeToMarinersWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_notice_to_mariners_if_enough_time_lapsed() {
      val mockNoticeToMarinersRepository = mockk<NoticeToMarinersRepository>()
      coEvery { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NOTICE_TO_MARINERS) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNoticeToMarinersWorkerFactory(
         repository = mockNoticeToMarinersRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNoticeToMarinersWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_notice_to_mariners_if_not_enough_time_lapsed() {
      val mockNoticeToMarinersRepository = mockk<NoticeToMarinersRepository>()
      coEvery { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NOTICE_TO_MARINERS) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNoticeToMarinersWorkerFactory(
         repository = mockNoticeToMarinersRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNoticeToMarinersWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { mockNoticeToMarinersRepository.fetchNoticeToMariners(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.NOTICE_TO_MARINERS, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}