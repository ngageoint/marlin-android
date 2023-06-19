package mil.nga.msi.network.port

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.port.Port
import mil.nga.msi.repository.port.PortRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PortServiceTest {

    @Test
    fun testRemoteDataSource() = runTest {
        val mockResponse = PortResponse(
            ports = listOf(
                Port(1, "1", 1.0, 1.0),
                Port(2, "2", 2.0, 2.0)
            )
        )

        val mockService = mockk<PortService>()
        coEvery {
            mockService.getPorts()
        } returns Response.success(mockResponse)

        val dataSource = PortRemoteDataSource(mockService)
        val ports = dataSource.fetchPorts()
        assertEquals(2, ports.size)
    }
}


