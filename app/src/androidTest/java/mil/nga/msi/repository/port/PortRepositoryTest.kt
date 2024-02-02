package mil.nga.msi.repository.port

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.testing.asPagingSourceFactory
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
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.UUID

class PortRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_notify_new_ports() = runTest {
      val remotePorts = listOf(
         Port(1, "1", 0.0, 0.0),
         Port(2, "2", 0.0, 0.0),
         Port(3, "3", 0.0, 0.0)
      )

      val localPorts = listOf(
         Port(1, "1", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<PortLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingPorts(any()) } returns localPorts
      coEvery { localDataSource.getPorts() } returns localPorts
      val scope = this
      coEvery { localDataSource.observePortListItems(any()) } answers {
         flowOf(emptyList<Port>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<PortRemoteDataSource>()
      coEvery { remoteDataSource.fetchPorts() } returns remotePorts

      val notification = mockk<MarlinNotification>()
      every { notification.port(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.PORT) } returns Instant.now()

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchPorts(refresh = true)

      verify {
         notification.port(remotePorts.minus(localPorts.toSet()))
      }
   }

   @Test
   fun should_add_returned_ports() = runTest {
      val remotePorts = listOf(
         Port(1, "1", 0.0, 0.0),
         Port(2, "2", 0.0, 0.0),
         Port(3, "3", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<PortLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingPorts(any()) } returns emptyList()
      coEvery { localDataSource.getPorts() } returns emptyList()
      val scope = this
      coEvery { localDataSource.observePortListItems(any()) } answers {
         flowOf(emptyList<Port>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<PortRemoteDataSource>()
      coEvery { remoteDataSource.fetchPorts() } returns remotePorts

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.PORT) } returns null

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mockk(),
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchPorts(refresh = true)

      coVerify {
         localDataSource.insert(remotePorts)
      }
   }

   @Test
   fun should_be_fetching_if_work_is_running() = runTest {
      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } answers  {
         val workInfo = WorkInfo(
            id = UUID.randomUUID(),
            state = WorkInfo.State.RUNNING,
            tags = emptySet()
         )
         flowOf(listOf(workInfo)).asLiveData()
      }

      val scope = this
      val localDataSource = mockk<PortLocalDataSource>()
      coEvery { localDataSource.observePortListItems(any()) } answers {
         flowOf(emptyList<Port>()).asPagingSourceFactory(scope)()
      }

      val viewModel = PortRepository(
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
            id = UUID.randomUUID(),
            state = WorkInfo.State.SUCCEEDED,
            tags = emptySet()
         )
         flowOf(listOf(workInfo)).asLiveData()
      }

      val scope = this
      val localDataSource = mockk<PortLocalDataSource>()
      coEvery { localDataSource.observePortListItems(any()) } answers {
         flowOf(emptyList<Port>()).asPagingSourceFactory(scope)()
      }

      val viewModel = PortRepository(
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