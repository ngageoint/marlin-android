package mil.nga.msi.network.asam

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.repository.asam.AsamRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class AsamServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val mockResponse = AsamResponse(
            asams = listOf(
                Asam("1", Date(), 1.0, 1.0),
                Asam("1", Date(), 1.0, 1.0)
            )
        )

        val mockService = mock<AsamService> {
            onBlocking {
                getAsams()
            } doReturn Response.success(mockResponse)
        }

        val repository = AsamRemoteDataSource(mockService)
        val asams = repository.fetchAsams()
        assertEquals(2, asams.size)
    }
}


