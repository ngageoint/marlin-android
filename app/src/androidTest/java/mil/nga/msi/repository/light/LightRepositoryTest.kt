package mil.nga.msi.repository.light

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
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.UUID

class LightRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_notify_new_lights() = runTest {
      val scope = this

      val remoteLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0),
         Light("2", "2", "2", 2, "01", "2023", 0.0, 0.0),
         Light("3", "3", "3", 3, "01", "2023", 0.0, 0.0)
      )

      val localLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<LightLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns emptyList()
      coEvery { localDataSource.existingLights(any()) } returns localLights
      coEvery { localDataSource.getLights() } returns localLights
      coEvery { localDataSource.observeLightListItems(any()) } answers {
         flowOf(emptyList<Light>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<LightRemoteDataSource>()
      coEvery { remoteDataSource.fetchLights(any()) } returns remoteLights

      val notification = mockk<MarlinNotification>()
      every { notification.light(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.LIGHT) } returns Instant.now()

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchLights(refresh = true)

      val difference = PublicationVolume.values().flatMap { _ ->
         remoteLights.minus(localLights.toSet())
      }

      verify {
         notification.light(difference)
      }
   }

   @Test
   fun should_add_returned_lights() = runTest {
      val remoteLights = listOf(
         Light("1", "1", "1", 1, "01", "2023", 0.0, 0.0),
         Light("2", "2", "2", 2, "01", "2023", 0.0, 0.0),
         Light("3", "3", "3", 3, "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<LightLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns emptyList()
      coEvery { localDataSource.existingLights(any()) } returns emptyList()
      coEvery { localDataSource.getLights() } returns emptyList()

      val remoteDataSource = mockk<LightRemoteDataSource>()
      coEvery { remoteDataSource.fetchLights(any()) } returns remoteLights

      val notification = mockk<MarlinNotification>()
      every { notification.light(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.LIGHT) } returns null

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchLights(refresh = true)

      coVerify(exactly = PublicationVolume.values().size) {
         localDataSource.insert(remoteLights)
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

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = mockk(),
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

      val viewModel = LightRepository(
         workManager = workManager,
         localDataSource = mockk(),
         remoteDataSource = mockk(),
         notification = mockk(),
         filterRepository = mockk(),
         userPreferencesRepository = mockk()
      )

      val fetching = viewModel.fetching.asFlow().first()
      Assert.assertEquals(false, fetching)
   }
}