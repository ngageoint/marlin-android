package mil.nga.msi.ui.asam

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.repository.asam.AsamRepository
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AsamViewModelTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun testGetAsam() = runTest {
      val asamRepository = mock<AsamRepository>()
      val viewModel = AsamViewModel(
         repository = asamRepository,
         tileProvider = mock()
      )
      viewModel.getAsam("1")

      verify(asamRepository).observeAsam("1")
   }
}