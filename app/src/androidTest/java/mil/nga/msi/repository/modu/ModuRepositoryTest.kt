package mil.nga.msi.repository.modu

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.Date
import java.util.UUID

class ModuRepositoryTest {

   @Test
   fun should_notify_new_modus() = runTest {
      val remoteModus = listOf(
         Modu("1", Date(), 0.0, 0.0),
         Modu("2", Date(), 0.0, 0.0),
         Modu("3", Date(), 0.0, 0.0)
      )

      val localModus = listOf(
         Modu("1", Date(), 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<ModuLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingModus(any()) } returns localModus
      coEvery { localDataSource.getModus() } returns localModus
      coEvery { localDataSource.observeModus() } answers { flowOf(emptyList()) }

      val remoteDataSource = mockk<ModuRemoteDataSource>()
      coEvery { remoteDataSource.fetchModus() } returns remoteModus

      val notification = mockk<MarlinNotification>()
      every { notification.modu(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.MODU) } returns Instant.now()

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchModus(refresh = true)

      verify {
         notification.modu(remoteModus.minus(localModus.toSet()))
      }
   }

   @Test
   fun should_add_returned_modus() = runTest {
      val remoteModus = listOf(
         Modu("1", Date(), 0.0, 0.0),
         Modu("2", Date(), 0.0, 0.0),
         Modu("3", Date(), 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<ModuLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingModus(any()) } returns emptyList()
      coEvery { localDataSource.getModus() } returns emptyList()
      coEvery { localDataSource.observeModus() } answers { flowOf(emptyList()) }

      val remoteDataSource = mockk<ModuRemoteDataSource>()
      coEvery { remoteDataSource.fetchModus() } returns remoteModus

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.MODU) } returns null

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mockk(),
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchModus(refresh = true)

      coVerify {
         localDataSource.insert(remoteModus)
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

      val localDataSource = mockk<ModuLocalDataSource>()
      coEvery { localDataSource.observeModus() } answers { flowOf(emptyList()) }

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = mockk(),
         notification = mockk(),
         filterRepository = mockk(),
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

      val localDataSource = mockk<ModuLocalDataSource>()
      coEvery { localDataSource.observeModus() } answers { flowOf(emptyList()) }

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = mockk(),
         notification = mockk(),
         filterRepository = mockk(),
         userPreferencesRepository = mockk()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(false, fetching)
   }
}