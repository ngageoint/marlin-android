package mil.nga.msi.repository.light

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyList
import org.mockito.kotlin.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class LightRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun should_notify_new_lights() = runTest {
      val remoteLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0),
         Light("2", "2", "2", 2, "01", "2023", 0.0, 0.0),
         Light("3", "3", "3", 3, "01", "2023", 0.0, 0.0)
      )

      val localLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<LightLocalDataSource>()
      `when`(localDataSource.insert(remoteLights)).thenAnswer { Unit }
      `when`(localDataSource.existingLights(anyList())).thenAnswer { localLights }
      `when`(localDataSource.getLights()).thenAnswer { localLights }

      val remoteDataSource = mock<LightRemoteDataSource>()
      `when`(remoteDataSource.fetchLights(any())).thenAnswer { remoteLights }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.LIGHT)).thenAnswer { Instant.now() }

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchLights(refresh = true)

      val difference = PublicationVolume.values().flatMap { _ ->
         remoteLights.minus(localLights.toSet())
      }

      verify(notification).light(difference)
   }

   @Test
   fun should_add_returned_lights() = runTest {
      val remoteLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0),
         Light("2", "2", "2", 2, "01", "2023", 0.0, 0.0),
         Light("3", "3", "3", 3, "01", "2023", 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<LightLocalDataSource>()
      `when`(localDataSource.insert(remoteLights)).thenAnswer { Unit }
      `when`(localDataSource.existingLights(anyList())).thenAnswer { emptyList<DgpsStation>() }
      `when`(localDataSource.getLights()).thenAnswer { emptyList<DgpsStation>() }

      val remoteDataSource = mock<LightRemoteDataSource>()
      `when`(remoteDataSource.fetchLights(any())).thenAnswer { remoteLights }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.DGPS_STATION)).thenAnswer { null }

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchLights(refresh = true)

      verify(localDataSource, times(PublicationVolume.values().size)).insert(remoteLights)
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

      val viewModel = LightRepository(
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

      val viewModel = LightRepository(
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