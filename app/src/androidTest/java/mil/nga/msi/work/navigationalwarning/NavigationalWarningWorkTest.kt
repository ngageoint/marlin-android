package mil.nga.msi.work.navigationalwarning

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRepository
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class RefreshNavigationalWarningWorkerFactory(
   private val repository: NavigationalWarningRepository,
   private val userPreferencesRepository: UserPreferencesRepository,
   private val notification: MarlinNotification
) : WorkerFactory() {
   override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
      return RefreshNavigationalWarningWorker(appContext, workerParameters, repository, userPreferencesRepository, notification)
   }
}

class NavigationalWarningWorkTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_fetch_navigational_warnings_if_never_fetched() {
      val navigationalWarningRepository = mockk<NavigationalWarningRepository>()
      coEvery { navigationalWarningRepository.fetchNavigationalWarnings(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } returns null
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNavigationalWarningWorkerFactory(
         repository = navigationalWarningRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNavigationalWarningWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { navigationalWarningRepository.fetchNavigationalWarnings(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_fetch_navigational_warnings_if_enough_time_lapsed() {
      val navigationalWarningRepository = mockk<NavigationalWarningRepository>()
      coEvery { navigationalWarningRepository.fetchNavigationalWarnings(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } returns Instant.now().minus(25L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNavigationalWarningWorkerFactory(
         repository = navigationalWarningRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNavigationalWarningWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify { navigationalWarningRepository.fetchNavigationalWarnings(true) }
         coVerify { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }

   @Test
   fun should_not_fetch_navigational_warnings_if_not_enough_time_lapsed() {
      val navigationalWarningRepository = mockk<NavigationalWarningRepository>()
      coEvery { navigationalWarningRepository.fetchNavigationalWarnings(true) } returns emptyList()

      val mockUserPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { mockUserPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } returns Instant.now().minus(23L, ChronoUnit.HOURS)
      coEvery { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) } returns Unit

      val context = InstrumentationRegistry.getInstrumentation().targetContext

      val factory = RefreshNavigationalWarningWorkerFactory(
         repository = navigationalWarningRepository,
         userPreferencesRepository = mockUserPreferencesRepository,
         notification = mockk()
      )

      val worker = TestListenableWorkerBuilder<RefreshNavigationalWarningWorker>(context)
         .setWorkerFactory(factory)
         .build()

      runBlocking {
         val result = worker.doWork()
         coVerify(exactly = 0) { navigationalWarningRepository.fetchNavigationalWarnings(true) }
         coVerify(exactly = 0) { mockUserPreferencesRepository.setFetched(DataSource.NAVIGATION_WARNING, any()) }
         assertEquals(ListenableWorker.Result.success(), result)
      }
   }
}