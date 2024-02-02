package mil.nga.msi.repository.dgpsstation

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
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.UUID

class DgpsStationRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_notify_new_stations() = runTest {
      val scope = this

      val remoteStations = listOf(
         DgpsStation("1", "1", 1f, "01", "2023", 0.0, 0.0),
         DgpsStation("2", "2", 2f, "01", "2023", 0.0, 0.0),
         DgpsStation("3", "3", 3f, "01", "2023", 0.0, 0.0)
      )

      val localStations = listOf(
         DgpsStation("1", "1", 1f, "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<DgpsStationLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingDgpsStations(any()) } returns localStations
      coEvery { localDataSource.getDgpsStations() } returns localStations
      coEvery { localDataSource.observeDgpsStationListItems(any()) } answers {
         flowOf(emptyList<DgpsStation>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<DgpsStationRemoteDataSource>()
      coEvery { remoteDataSource.fetchDgpsStations(any()) } returns remoteStations

      val notification = mockk<MarlinNotification>()
      every { notification.dgpsStation(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.DGPS_STATION) } returns Instant.now()

      val viewModel = DgpsStationRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchDgpsStations(refresh = true)

      val difference = PublicationVolume.entries.flatMap { _ ->
         remoteStations.minus(localStations.toSet())
      }

      verify {
         notification.dgpsStation(difference)
      }
   }

   @Test
   fun should_add_returned_stations() = runTest {
      val remoteStations = listOf(
         DgpsStation("1", "1", 1f, "01", "2023", 0.0, 0.0),
         DgpsStation("2", "2", 2f, "01", "2023", 0.0, 0.0),
         DgpsStation("3", "3", 3f, "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<DgpsStationLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingDgpsStations(any()) } returns emptyList()
      coEvery { localDataSource.getDgpsStations() } returns emptyList()

      val remoteDataSource = mockk<DgpsStationRemoteDataSource>()
      coEvery { remoteDataSource.fetchDgpsStations(any()) } returns remoteStations

      val notification = mockk<MarlinNotification>()
      every { notification.dgpsStation(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.DGPS_STATION) } returns null

      val viewModel = DgpsStationRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchDgpsStations(refresh = true)

      coVerify(exactly = PublicationVolume.entries.size) {
         localDataSource.insert(remoteStations)
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

      val viewModel = DgpsStationRepository(
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
            id = UUID.randomUUID(),
            state = WorkInfo.State.SUCCEEDED,
            tags = emptySet()
         )
         flowOf(listOf(workInfo)).asLiveData()
      }

      val viewModel = DgpsStationRepository(
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