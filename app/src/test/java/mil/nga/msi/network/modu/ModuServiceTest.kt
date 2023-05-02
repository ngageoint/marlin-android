package mil.nga.msi.network.modu

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.modu.ModuRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ModuServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val latestModuDate = dateFormat.parse("2021-04-16")!!
        val latestModu = Modu("1", latestModuDate, 1.0, 1.0)

        val mockResponse = ModuResponse(
            modus = listOf(
                Modu("1", Date(), 1.0, 1.0),
                Modu("2", Date(), 2.0, 2.0),
            )
        )

        val mockService = mock<ModuService> {
            onBlocking {
                getModus(
                    minDate = "2021-04-16",
                    maxDate = dateFormat.format(Date())
                )
            } doReturn Response.success(mockResponse)
        }

        val mockDataSource = mock<ModuLocalDataSource> {
            onBlocking {
                getLatestModu()
            } doReturn latestModu
        }

        val repository = ModuRemoteDataSource(mockService, mockDataSource)
        val modus = repository.fetchModus()
        assertEquals(2, modus.size)

        verify(mockService).getModus(
            sort = "date",
            output = "json",
            minDate = "2021-04-16",
            maxDate = dateFormat.format(Date())
        )
    }
}



