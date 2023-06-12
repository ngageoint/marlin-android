package mil.nga.msi.ui.asam

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.DataSource
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRepository
import mil.nga.msi.repository.preferences.FilterRepository
import mil.nga.msi.repository.preferences.SortRepository
import mil.nga.msi.ui.asam.list.AsamsViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import mil.nga.msi.sort.Sort
import mil.nga.msi.ui.asam.list.AsamListItem
import org.junit.Assert
import org.mockito.Mockito.`when`
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class AsamsViewModelTest {

   @Before
   fun setup() {
      MockitoAnnotations.openMocks(this)
   }

   @Test
   fun testGetAsam() = runTest {
      val filterRepository = mock<FilterRepository>()
      `when`(filterRepository.filters).thenReturn(emptyFlow())

      val sortRepository = mock<SortRepository>()
      `when`(sortRepository.sort).thenReturn(emptyFlow())

      val asamRepository = mock<AsamRepository>()

      val viewModel = AsamsViewModel(
         asamRepository = asamRepository,
         filterRepository = filterRepository,
         sortRepository = sortRepository
      )
      viewModel.getAsam("1")

      verify(asamRepository).getAsam("1")
   }

   @Test
   fun testAsamsFilter() = runTest {
      val filterRepository = mock<FilterRepository>()
      `when`(filterRepository.filters).thenReturn(
         flowOf(mapOf(DataSource.ASAM to emptyList()))
      )

      val sortRepository = mock<SortRepository>()
      `when`(sortRepository.sort).thenReturn(
         flowOf(mapOf(DataSource.ASAM to Sort(false, emptyList())))
      )

      val asamRepository = mock<AsamRepository>()
      `when`(asamRepository.observeAsamListItems(Mockito.anyList(), Mockito.anyList()))
         .thenAnswer {
            val asamsFlow = flowOf(
               listOf(
                  Asam(
                     reference = "1",
                     date = Date(),
                     latitude = 0.0,
                     longitude = 0.0
                  )
               )
            )

            val pagingSourceFactory = asamsFlow.asPagingSourceFactory(coroutineScope = this)
            pagingSourceFactory()
         }

      val viewModel = AsamsViewModel(
         asamRepository = asamRepository,
         filterRepository = filterRepository,
         sortRepository = sortRepository
      )

      val asams: Flow<PagingData<AsamListItem>> = viewModel.asams
      val snapshot: List<AsamListItem> = asams.asSnapshot() {}

      Assert.assertEquals(1, snapshot.size)
   }
}