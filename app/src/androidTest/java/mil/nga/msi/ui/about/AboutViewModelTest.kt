//package mil.nga.msi.ui.about
//
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.emptyFlow
//import kotlinx.coroutines.test.runTest
//import mil.nga.msi.repository.preferences.UserPreferencesRepository
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.`when`
//import org.mockito.MockitoAnnotations
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class AboutViewModelTest {
//
//   @Before
//   fun setup() {
//      MockitoAnnotations.openMocks(this)
//   }
//
//   @Test
//   fun testSetDeveloperMode() = runTest {
//      val mockRepository = mock<UserPreferencesRepository>()
//      `when`(mockRepository.developer()).thenReturn(emptyFlow())
//
//      val viewModel = AboutViewModel(mockRepository)
//      viewModel.setDeveloperMode()
//
//      verify(mockRepository).setDeveloperMode()
//   }
//}