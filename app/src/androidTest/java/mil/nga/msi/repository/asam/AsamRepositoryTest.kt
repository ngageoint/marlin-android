package mil.nga.msi.repository.asam

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
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.asam.AsamListItem
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.Date
import java.util.UUID

class AsamRepositoryTest {

   @After
   fun tearDown() {
      clearAllMocks()
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

      val workManager = mockk<WorkManager>()
      every {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()

      val localDataSource = mockk<AsamLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns emptyList()
      coEvery { localDataSource.existingAsams(any()) } returns localAsams
      coEvery { localDataSource.getAsams() } returns localAsams
      val scope = this
      coEvery { localDataSource.observeAsams() } answers {
         flowOf(emptyList<AsamListItem>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<AsamRemoteDataSource>()
      coEvery { remoteDataSource.fetchAsams() } returns remoteAsams

      val notification = mockk<MarlinNotification>()
      every { notification.asam(any()) } returns Unit

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.ASAM) } returns Instant.now()

      val viewModel = AsamRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchAsams(refresh = true)

      verify {
         notification.asam(remoteAsams.minus(localAsams.toSet()))
      }
   }

   @Test
   fun should_add_returned_asams() = runTest {
      val remoteAsams = listOf(
         Asam("1", Date(), 0.0, 0.0),
         Asam("2", Date(), 0.0, 0.0),
         Asam("3", Date(), 0.0, 0.0)
      )

      val workManager = mockk<WorkManager>()
      coEvery {
         workManager.getWorkInfosForUniqueWorkLiveData(any())
      } returns emptyFlow<List<WorkInfo>>().asLiveData()


      val localDataSource = mockk<AsamLocalDataSource>()
      coEvery { localDataSource.insert(any()) } returns emptyList()
      coEvery { localDataSource.existingAsams(any()) } returns emptyList()
      coEvery { localDataSource.getAsams() } returns emptyList()
      val scope = this
      coEvery { localDataSource.observeAsams() } answers {
         flowOf(emptyList<AsamListItem>()).asPagingSourceFactory(scope)()
      }

      val remoteDataSource = mockk<AsamRemoteDataSource>()
      coEvery { remoteDataSource.fetchAsams() } returns remoteAsams

      val userPreferencesRepository = mockk<UserPreferencesRepository>()
      coEvery { userPreferencesRepository.fetched(DataSource.ASAM) } returns null

      val viewModel = AsamRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mockk(),
         filterRepository = mockk(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchAsams(refresh = true)

      coVerify {
         localDataSource.insert(remoteAsams)
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
      val localDataSource = mockk<AsamLocalDataSource>()
      coEvery { localDataSource.observeAsams() } answers {
         flowOf(emptyList<AsamListItem>()).asPagingSourceFactory(scope)()
      }

      val viewModel = AsamRepository(
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
      val localDataSource = mockk<AsamLocalDataSource>()
      coEvery { localDataSource.observeAsams() } answers {
         flowOf(emptyList<AsamListItem>()).asPagingSourceFactory(scope)()
      }

      val viewModel = AsamRepository(
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