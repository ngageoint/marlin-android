package mil.nga.msi.network.asam

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Date

class AsamServiceTest {

    @Test
    fun testRemoteDataSource() = runTest {
        val mockResponse = AsamResponse(
            asams = listOf(
                Asam("1", Date(), 1.0, 1.0),
                Asam("1", Date(), 1.0, 1.0)
            )
        )

        val mockService = mockk<AsamService>()
        coEvery { mockService.getAsams() } returns Response.success(mockResponse)

        val repository = AsamRemoteDataSource(mockService)
        val asams = repository.fetchAsams()
        assertEquals(2, asams.size)
    }
}


