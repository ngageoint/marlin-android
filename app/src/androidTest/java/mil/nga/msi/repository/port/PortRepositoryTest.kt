package mil.nga.msi.repository.port

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class PortRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
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

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<PortLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingPorts(Mockito.anyList())).thenAnswer { localPorts }
      `when`(localDataSource.getPorts()).thenAnswer { localPorts }

      val remoteDataSource = mock<PortRemoteDataSource>()
      `when`(remoteDataSource.fetchPorts()).thenAnswer { remotePorts }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.PORT)).thenAnswer { Instant.now() }

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchPorts(refresh = true)

      verify(notification).port(remotePorts.minus(localPorts.toSet()))
   }

   @Test
   fun should_add_returned_ports() = runTest {
      val remotePorts = listOf(
         Port(1, "1", 0.0, 0.0),
         Port(2, "2", 0.0, 0.0),
         Port(3, "3", 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<PortLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingPorts(Mockito.anyList())).thenAnswer { emptyList<Modu>() }
      `when`(localDataSource.getPorts()).thenAnswer { emptyList<Modu>() }

      val remoteDataSource = mock<PortRemoteDataSource>()
      `when`(remoteDataSource.fetchPorts()).thenAnswer { remotePorts }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.PORT)).thenAnswer { null }

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchPorts(refresh = true)

      verify(localDataSource).insert(remotePorts)
   }

   @Test
   fun should_be_fetching_if_work_is_running() = runTest {
      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
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

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = mock(),
         remoteDataSource = mock(),
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = mock()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(true, fetching)
   }

   @Test
   fun should_not_be_fetching_if_work_is_not_running() = runTest {
      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
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

      val viewModel = PortRepository(
         workManager = workManager,
         localDataSource = mock(),
         remoteDataSource = mock(),
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = mock()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(false, fetching)
   }
}