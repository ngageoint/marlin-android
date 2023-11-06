package mil.nga.msi.work.dgpsstation

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
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class LoadDgpsStationWorkerFactory(private val dataSource: DgpsStationLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return LoadDgpsStationWorker(appContext, workerParameters, dataSource)
   }
}

class RefreshDgpsStationWorkerFactory(
   private val repository: DgpsStationRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshDgpsStationWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class DgpsStationWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_dgps_stations() {
      val mockDataSource = mockk<DgpsStationLocalDataSource>()
      every { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadDgpsStationWorker>(context)
         .setWorkerFactory(LoadDgpsStationWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_dgps_stations() {
      val mockDataSource = mockk<DgpsStationLocalDataSource>()
      every { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadDgpsStationWorker>(context)
         .setWorkerFactory(LoadDgpsStationWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_dgps_stations_if_never_fetched() {
      val mockDgpsStationRepository = mockk<DgpsStationRepository>()
      coEvery { mockDgpsStationRepository.fetchDgpsStations(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.DGPS_STATION) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshDgpsStationWorkerFactory(
         repository = mockDgpsStationRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshDgpsStationWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDgpsStationRepository.fetchDgpsStations(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_dgps_stations_if_enough_time_lapsed() {
      val mockDgpsStationRepository = mockk<DgpsStationRepository>()
      coEvery { mockDgpsStationRepository.fetchDgpsStations(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.DGPS_STATION) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshDgpsStationWorkerFactory(
         repository = mockDgpsStationRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshDgpsStationWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDgpsStationRepository.fetchDgpsStations(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_dgps_stations_if_not_enough_time_lapsed() {
      val mockDgpsStationRepository = mockk<DgpsStationRepository>()
      coEvery { mockDgpsStationRepository.fetchDgpsStations(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.DGPS_STATION) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshDgpsStationWorkerFactory(
         repository = mockDgpsStationRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshDgpsStationWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { mockDgpsStationRepository.fetchDgpsStations(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.DGPS_STATION, any()) }
         TestCase.assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}