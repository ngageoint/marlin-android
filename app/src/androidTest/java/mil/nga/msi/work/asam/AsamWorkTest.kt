package mil.nga.msi.work.asam

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
import mil.nga.msi.repository.asam.AsamLocalDataSource
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class LoadAsamWorkerFactory(private val dataSource: AsamLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return LoadAsamWorker(appContext, workerParameters, dataSource)
   }
}

class RefreshAsamWorkerFactory(
   private val repository: AsamRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshAsamWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class AsamWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_asams() {
      val mockDataSource = mockk<AsamLocalDataSource>()
      every { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns emptyList()

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadAsamWorker>(context)
         .setWorkerFactory(LoadAsamWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_asams() {
      val mockDataSource = mockk<AsamLocalDataSource>()
      every { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadAsamWorker>(context)
         .setWorkerFactory(LoadAsamWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_asams_if_never_fetched() {
      val mockAsamRepository = mockk<AsamRepository>()
      coEvery { mockAsamRepository.fetchAsams(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.ASAM) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshAsamWorkerFactory(
         repository = mockAsamRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshAsamWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockAsamRepository.fetchAsams(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_asams_if_enough_time_lapsed() {
      val mockAsamRepository = mockk<AsamRepository>()
      coEvery { mockAsamRepository.fetchAsams(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.ASAM) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshAsamWorkerFactory(
         repository = mockAsamRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshAsamWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockAsamRepository.fetchAsams(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_asams_if_not_enough_time_lapsed() {
      val mockAsamRepository = mockk<AsamRepository>()
      coEvery { mockAsamRepository.fetchAsams(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.ASAM) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshAsamWorkerFactory(
         repository = mockAsamRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshAsamWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { mockAsamRepository.fetchAsams(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.ASAM, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}