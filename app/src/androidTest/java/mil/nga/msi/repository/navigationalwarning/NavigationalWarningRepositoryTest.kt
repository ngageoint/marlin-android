package mil.nga.msi.repository.navigationalwarning

import androidx.lifecycle.asLiveData
import androidx.work.WorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import mil.nga.msi.MarlinNotification
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.preferences.UserPreferencesRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningRepositoryTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun should_notify_new_navigational_warnings() = runTest {
      val remoteWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
      )

      val localWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<NavigationalWarningLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.existingNavigationalWarnings(Mockito.anyList())).thenAnswer { localWarnings }
      `when`(localDataSource.getNavigationalWarnings()).thenAnswer { localWarnings }

      val remoteDataSource = mock<NavigationalWarningRemoteDataSource>()
      `when`(remoteDataSource.fetchNavigationalWarnings()).thenAnswer { remoteWarnings }

      val notification = mock<MarlinNotification>()

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)).thenAnswer {
         Instant.now()
      }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = notification,
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      verify(notification).navigationWarning(remoteWarnings.minus(localWarnings.toSet()))
   }

   @Test
   fun should_delete_unreturned_navigational_warnings() = runTest {
      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<NavigationalWarningLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.getNavigationalWarnings()).thenAnswer {
         listOf(
            NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date())
         )
      }

      val remoteDataSource = mock<NavigationalWarningRemoteDataSource>()
      `when`(remoteDataSource.fetchNavigationalWarnings()).thenAnswer {
         listOf(
            NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
            NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
         )
      }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)).thenAnswer { null }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      verify(localDataSource).deleteNavigationalWarnings(listOf(1))
   }

   @Test
   fun should_add_returned_navigational_warnings() = runTest {
      val remoteWarnings = listOf(
         NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date()),
         NavigationalWarning("3", 3, 2023, NavigationArea.HYDROARC, Date())
      )

      val workManager = mock<WorkManager>()
      `when`(workManager.getWorkInfosForUniqueWorkLiveData(Mockito.any())).thenAnswer {
         emptyFlow<Boolean>().asLiveData()
      }

      val localDataSource = mock<NavigationalWarningLocalDataSource>()
      `when`(localDataSource.insert(Mockito.anyList())).thenAnswer { Unit }
      `when`(localDataSource.getNavigationalWarnings()).thenAnswer { emptyList<NavigationalWarning>() }

      val remoteDataSource = mock<NavigationalWarningRemoteDataSource>()
      `when`(remoteDataSource.fetchNavigationalWarnings()).thenAnswer { remoteWarnings }

      val userPreferencesRepository = mock<UserPreferencesRepository>()
      `when`(userPreferencesRepository.fetched(DataSource.NAVIGATION_WARNING)).thenAnswer { null }

      val viewModel = NavigationalWarningRepository(
         workManager = workManager,
         localDataSource = localDataSource,
         remoteDataSource = remoteDataSource,
         notification = mock(),
         userPreferencesRepository = userPreferencesRepository
      )

      viewModel.fetchNavigationalWarnings(refresh = true)

      verify(localDataSource).insert(remoteWarnings)
   }
}