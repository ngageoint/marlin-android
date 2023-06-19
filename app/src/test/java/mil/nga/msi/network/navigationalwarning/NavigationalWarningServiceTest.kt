package mil.nga.msi.network.navigationalwarning

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.navigationwarning.NavigationArea
import mil.nga.msi.datasource.navigationwarning.NavigationalWarning
import mil.nga.msi.repository.navigationalwarning.NavigationalWarningRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationalWarningServiceTest {

    @Test
    fun testRemoteDataSource() = runTest {
        val mockResponse = NavigationalWarningResponse(
            warnings = listOf(
                NavigationalWarning("1", 1, 2023, NavigationArea.HYDROARC, Date()),
                NavigationalWarning("2", 2, 2023, NavigationArea.HYDROARC, Date())
            )
        )

        val mockService = mockk<NavigationalWarningService>()
        coEvery {
            mockService.getNavigationalWarnings()
        } returns Response.success(mockResponse)

        val dataSource = NavigationalWarningRemoteDataSource(mockService)
        val warnings = dataSource.fetchNavigationalWarnings()
        assertEquals(2, warnings.size)
    }
}


