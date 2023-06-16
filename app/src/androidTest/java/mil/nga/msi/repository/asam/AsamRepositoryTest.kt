package mil.nga.msi.repository.asam

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
import mil.nga.msi.datasource.asam.Asam
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
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AsamRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun should_notify_new_asams() = runTest {
      val remoteAsams = listOf(
         Asam("1", Date(), 0.0, 0.0),
         Asam("2", Date(), 0.0, 0.0),
         Asam("3", Date(), 0.0, 0.0)
      )

      val localAsams = listOf(
         Asam("1", Date(), 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<AsamLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingAsams(Mockito.anyList())).thenAnswer { localAsams }
      `when`(localDataSource.getAsams()).thenAnswer { localAsams }

      val remoteDataSource = mock<AsamRemoteDataSource>()
      `when`(remoteDataSource.fetchAsams()).thenAnswer { remoteAsams }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.ASAM)).thenAnswer { Instant.now() }

      val viewModel = AsamRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchAsams(refresh = true)

      verify(notification).asam(remoteAsams.minus(localAsams.toSet()))
   }

   @Test
   fun should_add_returned_asams() = runTest {
      val remoteAsams = listOf(
         Asam("1", Date(), 0.0, 0.0),
         Asam("2", Date(), 0.0, 0.0),
         Asam("3", Date(), 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<AsamLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingAsams(Mockito.anyList())).thenAnswer { emptyList<Asam>() }
      `when`(localDataSource.getAsams()).thenAnswer { emptyList<Asam>() }

      val remoteDataSource = mock<AsamRemoteDataSource>()
      `when`(remoteDataSource.fetchAsams()).thenAnswer { remoteAsams }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.ASAM)).thenAnswer { null }

      val viewModel = AsamRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchAsams(refresh = true)

      verify(localDataSource).insert(remoteAsams)
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

      val viewModel = AsamRepository(
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

      val viewModel = AsamRepository(
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