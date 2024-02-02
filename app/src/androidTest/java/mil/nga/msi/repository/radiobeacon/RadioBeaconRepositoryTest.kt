package mil.nga.msi.repository.radiobeacon

import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.testing.asPagingSourceFactory
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
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.UUID

class RadioBeaconRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
   }

   @Test
   fun should_notify_new_beacons() = runTest {
      val scope = this

      val remoteBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
         RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
         RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0)
      )

      val localBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<RadioBeaconLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingRadioBeacons(any()) } returns localBeacons
      coEvery { localDataSource.getRadioBeacons() } returns localBeacons
      coEvery { localDataSource.observeRadioBeaconListItems(any()) } answers {
         flowOf(emptyList<RadioBeacon>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<RadioBeaconRemoteDataSource>()
      coEvery { remoteDataSource.fetchRadioBeacons(any()) } returns remoteBeacons

      val notification = mockk<MarlinNotification>()
      every { notification.radioBeacon(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.RADIO_BEACON) } returns Instant.now()

      val viewModel = RadioBeaconRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchRadioBeacons(refresh = true)

      val difference = PublicationVolume.entries.flatMap { _ ->
         remoteBeacons.minus(localBeacons.toSet())
      }

      verify {
         notification.radioBeacon(difference)
      }
   }

   @Test
   fun should_add_returned_beacons() = runTest {
      val remoteBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
         RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
         RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<RadioBeaconLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns Unit
      coEvery { localDataSource.existingRadioBeacons(any()) } returns emptyList()
      coEvery { localDataSource.getRadioBeacons() } returns emptyList()
      val scope = this
      coEvery { localDataSource.observeRadioBeaconListItems(any()) } answers {
         flowOf(emptyList<RadioBeacon>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<RadioBeaconRemoteDataSource>()
      coEvery { remoteDataSource.fetchRadioBeacons(any()) } returns remoteBeacons

      val notification = mockk<MarlinNotification>()
      every { notification.radioBeacon(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.RADIO_BEACON) } returns null

      val viewModel = RadioBeaconRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchRadioBeacons(refresh = true)

      coVerify {
         localDataSource.insert(remoteBeacons)
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
      val localDataSource = mockk<RadioBeaconLocalDataSource>()
      coEvery { localDataSource.observeRadioBeaconListItems(any()) } answers {
         flowOf(emptyList<RadioBeacon>()).asPagingSourceFactory(scope)()
      }

      val viewModel = RadioBeaconRepository(
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
      val localDataSource = mockk<RadioBeaconLocalDataSource>()
      coEvery { localDataSource.observeRadioBeaconListItems(any()) } answers {
         flowOf(emptyList<RadioBeacon>()).asPagingSourceFactory(scope)()
      }

      val viewModel = RadioBeaconRepository(
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