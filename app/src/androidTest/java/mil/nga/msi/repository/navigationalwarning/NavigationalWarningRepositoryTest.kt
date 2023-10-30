package mil.nga.msi.repository.navigationalwarning

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.Date
import java.util.UUID

class NavigationalWarningRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_notify_new_navigational_warnings() = runTest {
      val remoteWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
      )

      val localWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<NavigationalWarningLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.deleteNavigationalWarnings(any()) } returns Unit
      coEvery { localDataSource.existingNavigationalWarnings(any()) } returns localWarnings
      coEvery { localDataSource.getNavigationalWarnings() } returns localWarnings

      val remoteDataSource = mockk<NavigationalWarningRemoteDataSource>()
      coEvery { remoteDataSource.fetchNavigationalWarnings() } returns remoteWarnings

      val notification = mockk<MarlinNotification>()
      every { notification.navigationWarning(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } returns Instant.now()

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      verify {
         notification.navigationWarning(remoteWarnings.minus(localWarnings.toSet()))
      }
   }

   @Test
   fun should_delete_unreturned_navigational_warnings() = runTest {
      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<NavigationalWarningLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.deleteNavigationalWarnings(any()) } returns Unit
      coEvery { localDataSource.getNavigationalWarnings() } coAnswers {
         listOf(
            NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
         )
      }

      val remoteDataSource = mockk<NavigationalWarningRemoteDataSource>()
      coEvery {remoteDataSource.fetchNavigationalWarnings()  } coAnswers {
         listOf(
            NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
         )
      }

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } coAnswers {
         null
      }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      coVerify {
         localDataSource.deleteNavigationalWarnings(listOf(1))
      }
   }

   @Test
   fun should_add_returned_navigational_warnings() = runTest {
      val remoteWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<NavigationalWarningLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.deleteNavigationalWarnings(any()) } returns Unit
      coEvery { localDataSource.existingNavigationalWarnings(any()) } returns emptyList()
      coEvery { localDataSource.getNavigationalWarnings() } returns emptyList()

      val remoteDataSource = mockk<NavigationalWarningRemoteDataSource>()
      coEvery { remoteDataSource.fetchNavigationalWarnings() } returns remoteWarnings

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING) } returns null

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      coVerify {
         localDataSource.insert(remoteWarnings)
      }
   }

   @Test
   fun should_be_fetching_if_work_is_running() = runTest {
      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } answers  {
         val workInfo = WorkInfo(
            UUID.randomUUID(),
            WorkInfo.State.RUNNING,
            Data(emptyMap<String, String>()),
            emptyList(),
            Data(emptyMap<String, String>()),
            0,
            0
         )
         flowOf(listOf(workInfo)).asLiveData()
      }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = mockk(),
         remoteDataSource = mockk(),
         notification = mockk(),
         userPreferencesRepository = mockk()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(true, fetching)
   }

   @Test
   fun should_not_be_fetching_if_work_is_not_running() = runTest {
      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } answers {
         val workInfo = WorkInfo(
            UUID.randomUUID(),
            WorkInfo.State.SUCCEEDED,
            Data(emptyMap<String, String>()),
            emptyList(),
            Data(emptyMap<String, String>()),
            0,
            0
         )
         flowOf(listOf(workInfo)).asLiveData()
      }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = mockk(),
         remoteDataSource = mockk(),
         notification = mockk(),
         userPreferencesRepository = mockk()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(false, fetching)
   }
}