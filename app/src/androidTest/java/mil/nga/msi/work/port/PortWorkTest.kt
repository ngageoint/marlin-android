package mil.nga.msi.work.port

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
import mil.nga.msi.repository.port.PortLocalDataSource
import mil.nga.msi.repository.port.PortRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class LoadPortWorkerFactory(private val dataSource: PortLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return LoadPortWorker(appContext, workerParameters, dataSource)
   }
}

class RefreshPortWorkerFactory(
   private val repository: PortRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshPortWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class PortWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_ports() {
      val mockDataSource = mockk<PortLocalDataSource>()
      every { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadPortWorker>(context)
         .setWorkerFactory(LoadPortWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_ports() {
      val mockDataSource = mockk<PortLocalDataSource>()
      every { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadPortWorker>(context)
         .setWorkerFactory(LoadPortWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_ports_if_never_fetched() {
      val mockPortRepository = mockk<PortRepository>()
      coEvery { mockPortRepository.fetchPorts(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.PORT) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshPortWorkerFactory(
         repository = mockPortRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshPortWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockPortRepository.fetchPorts(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_ports_if_enough_time_lapsed() {
      val mockPortRepository = mockk<PortRepository>()
      coEvery { mockPortRepository.fetchPorts(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.PORT) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshPortWorkerFactory(
         repository = mockPortRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshPortWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockPortRepository.fetchPorts(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_ports_if_not_enough_time_lapsed() {
      val mockPortRepository = mockk<PortRepository>()
      coEvery { mockPortRepository.fetchPorts(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.PORT) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshPortWorkerFactory(
         repository = mockPortRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshPortWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { mockPortRepository.fetchPorts(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.PORT, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}