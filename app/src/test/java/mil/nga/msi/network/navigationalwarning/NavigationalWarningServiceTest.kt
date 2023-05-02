package mil.nga.msi.network.navigationalwarning

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val mockResponse = NavigationalWarningResponse(
            warnings = listOf(
                NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
                NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date())
            )
        )

        val mockService = mock<NavigationalWarningService> {
            onBlocking {
                getNavigationalWarnings()
            } doReturn Response.success(mockResponse)
        }

        val dataSource = NavigationalWarningRemoteDataSource(mockService)
        val warnings = dataSource.fetchNavigationalWarnings()
        assertEquals(2, warnings.size)
    }
}


