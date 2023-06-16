package mil.nga.msi.repository.modu

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
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ModuRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

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

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<ModuLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingModus(Mockito.anyList())).thenAnswer { localModus }
      `when`(localDataSource.getModus()).thenAnswer { localModus }

      val remoteDataSource = mock<ModuRemoteDataSource>()
      `when`(remoteDataSource.fetchModus()).thenAnswer { remoteModus }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.MODU)).thenAnswer { Instant.now() }

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchModus(refresh = true)

      verify(notification).modu(remoteModus.minus(localModus.toSet()))
   }

   @Test
   fun should_add_returned_modus() = runTest {
      val remoteModus = listOf(
         Modu("1", Date(), 0.0, 0.0),
         Modu("2", Date(), 0.0, 0.0),
         Modu("3", Date(), 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<ModuLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingModus(Mockito.anyList())).thenAnswer { emptyList<Modu>() }
      `when`(localDataSource.getModus()).thenAnswer { emptyList<Modu>() }

      val remoteDataSource = mock<ModuRemoteDataSource>()
      `when`(remoteDataSource.fetchModus()).thenAnswer { remoteModus }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.MODU)).thenAnswer { null }

      val viewModel = ModuRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchModus(refresh = true)

      verify(localDataSource).insert(remoteModus)
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

      val viewModel = ModuRepository(
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

      val viewModel = ModuRepository(
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