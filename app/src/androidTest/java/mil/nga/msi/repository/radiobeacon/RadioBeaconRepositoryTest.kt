package mil.nga.msi.repository.radiobeacon

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
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
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
class RadioBeaconRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun should_notify_new_beacons() = runTest {
      val remoteBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
         RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
         RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0)
      )

      val localBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<RadioBeaconLocalDataSource>()
      `when`(localDataSource.insert(remoteBeacons)).thenAnswer { Unit }
      `when`(localDataSource.existingRadioBeacons(anyList())).thenAnswer { localBeacons }
      `when`(localDataSource.getRadioBeacons()).thenAnswer { localBeacons }

      val remoteDataSource = mock<RadioBeaconRemoteDataSource>()
      `when`(remoteDataSource.fetchRadioBeacons(any())).thenAnswer { remoteBeacons }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.RADIO_BEACON)).thenAnswer { Instant.now() }

      val viewModel = RadioBeaconRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchRadioBeacons(refresh = true)

      val difference = PublicationVolume.values().flatMap { _ ->
         remoteBeacons.minus(localBeacons.toSet())
      }

      verify(notification).radioBeacon(difference)
   }

   @Test
   fun should_add_returned_beacons() = runTest {
      val remoteBeacons = listOf(
         RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
         RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
         RadioBeacon("3", "3", "3", "01", "2023", 0.0, 0.0)
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<RadioBeaconLocalDataSource>()
      `when`(localDataSource.insert(remoteBeacons)).thenAnswer { Unit }
      `when`(localDataSource.existingRadioBeacons(anyList())).thenAnswer { emptyList<DgpsStation>() }
      `when`(localDataSource.getRadioBeacons()).thenAnswer { emptyList<DgpsStation>() }

      val remoteDataSource = mock<RadioBeaconRemoteDataSource>()
      `when`(remoteDataSource.fetchRadioBeacons(any())).thenAnswer { remoteBeacons }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.RADIO_BEACON)).thenAnswer { null }

      val viewModel = RadioBeaconRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         filterRepository = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchRadioBeacons(refresh = true)

      verify(localDataSource, times(PublicationVolume.values().size)).insert(remoteBeacons)
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

      val viewModel = RadioBeaconRepository(
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

      val viewModel = RadioBeaconRepository(
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