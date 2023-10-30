package mil.nga.msi.work.radiobeacon

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
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import mil.nga.msi.repository.radiobeacon.RadioBeaconRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class LoadRadioBeaconWorkerFactory(private val dataSource: RadioBeaconLocalDataSource) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return LoadRadioBeaconWorker(appContext, workerParameters, dataSource)
   }
}

class RefreshRadioBeaconWorkerFactory(
   private val repository: RadioBeaconRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshRadioBeaconWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class RadioBeaconWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_load_radio_beacons() {
      val mockDataSource = mockk<RadioBeaconLocalDataSource>()
      every { mockDataSource.isEmpty() } returns true
      coEvery { mockDataSource.insert(any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadRadioBeaconWorker>(context)
         .setWorkerFactory(LoadRadioBeaconWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_load_radio_beacons() {
      val mockDataSource = mockk<RadioBeaconLocalDataSource>()
      every { mockDataSource.isEmpty() } returns false

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val worker = TestListenableWorkerBuilder<LoadRadioBeaconWorker>(context)
         .setWorkerFactory(LoadRadioBeaconWorkerFactory(mockDataSource))
         .build()

      runBlocking {
         val result = worker.doWork()

         coVerify(exactly = 0) { mockDataSource.insert(any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_radio_beacons_if_never_fetched() {
      val mockRadioBeaconRepository = mockk<RadioBeaconRepository>()
      coEvery { mockRadioBeaconRepository.fetchRadioBeacons(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.RADIO_BEACON) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshRadioBeaconWorkerFactory(
         repository = mockRadioBeaconRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshRadioBeaconWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockRadioBeaconRepository.fetchRadioBeacons(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_radio_beacons_if_enough_time_lapsed() {
      val mockRadioBeaconRepository = mockk<RadioBeaconRepository>()
      coEvery { mockRadioBeaconRepository.fetchRadioBeacons(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.RADIO_BEACON) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshRadioBeaconWorkerFactory(
         repository = mockRadioBeaconRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshRadioBeaconWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { mockRadioBeaconRepository.fetchRadioBeacons(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_radio_beacons_if_not_enough_time_lapsed() {
      val mockRadioBeaconRepository = mockk<RadioBeaconRepository>()
      coEvery { mockRadioBeaconRepository.fetchRadioBeacons(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.RADIO_BEACON) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshRadioBeaconWorkerFactory(
         repository = mockRadioBeaconRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshRadioBeaconWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { mockRadioBeaconRepository.fetchRadioBeacons(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.RADIO_BEACON, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}