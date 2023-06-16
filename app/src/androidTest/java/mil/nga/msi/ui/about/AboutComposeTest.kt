package mil.nga.msi.ui.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.emptyFlow
import mil.nga.msi.type.Developer
import mil.nga.msi.ui.theme.MsiTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@HiltAndroidTest
class AboutComposeTest {

   @get:Rule
   val composeRule = createComposeRule()

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun navigation_warning_should_be_hidden() {
      val mockDeveloper = mock<Developer>()
      `when`(mockDeveloper.showDeveloperMode).thenReturn(false)

      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { MutableLiveData(mockDeveloper) }

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = {},
               onContact = {},
               onDisclaimer = {},
               viewModel = mockViewModel
            )
         }
      }

      composeRule.onNodeWithTag("navigation_warning_surface").assertDoesNotExist()
   }

   @Test
   fun navigation_warning_should_be_visible() {
      val mockDeveloper = mock<Developer>()
      `when`(mockDeveloper.showDeveloperMode).thenReturn(true)

      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { MutableLiveData(mockDeveloper) }

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = {},
               onContact = {},
               onDisclaimer = {},
               viewModel = mockViewModel
            )
         }
      }

      composeRule.onNodeWithTag("navigation_warning_surface").assertIsDisplayed()
   }

   @Test
   fun developer_mode_should_enable() {
      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { emptyFlow<Developer>().asLiveData() }

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = {},
               onContact = {},
               onDisclaimer = {},
               viewModel = mockViewModel
            )
         }
      }

      val version = composeRule.onNodeWithTag("version_column")
      (1..5).forEach { _ -> version.performClick() }

      verify(mockViewModel).setDeveloperMode()
   }

   @Test
   fun should__invoke_on_close() {
      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { emptyFlow<Developer>().asLiveData() }

      val mockClose = mock<() -> Unit>()

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = mockClose,
               onContact = {},
               onDisclaimer = {},
               viewModel = mockViewModel
            )
         }
      }

      composeRule.onNodeWithContentDescription("Navigation").performClick()

      verify(mockClose).invoke()
   }

   @Test
   fun should_invoke_on_contact() {
      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { emptyFlow<Developer>().asLiveData() }

      val mockContact = mock<() -> Unit>()

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = {},
               onContact = mockContact,
               onDisclaimer = { },
               viewModel = mockViewModel
            )
         }
      }

      composeRule.onNodeWithTag("contact_column").performClick()

      verify(mockContact).invoke()
   }

   @Test
   fun should_invoke_on_disclaimer() {
      val mockViewModel = mock<AboutViewModel>()
      `when`(mockViewModel.developer).thenAnswer { emptyFlow<Developer>().asLiveData() }

      val mockDisclaimer = mock<() -> Unit>()

      composeRule.setContent {
         MsiTheme {
            AboutScreen(
               onClose = {},
               onContact = {},
               onDisclaimer = mockDisclaimer,
               viewModel = mockViewModel
            )
         }
      }

      composeRule.onNodeWithTag("disclaimer_column").performClick()

      verify(mockDisclaimer).invoke()
   }
}