package mil.nga.msi.network.modu

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.modu.ModuRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ModuServiceTest {

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

        val mockService = mockk<ModuService>()
        coEvery {
            mockService.getModus(
                minDate = "2021-04-16",
                maxDate = dateFormat.format(Date())
            )
        } returns Response.success(mockResponse)

        val mockDataSource = mockk<ModuLocalDataSource>()
        coEvery {
            mockDataSource.getLatestModu()
        } returns latestModu

        val repository = ModuRemoteDataSource(mockService, mockDataSource)
        val modus = repository.fetchModus()
        assertEquals(2, modus.size)

        coVerify {
            mockService.getModus(
                sort = "date",
                output = "json",
                minDate = "2021-04-16",
                maxDate = dateFormat.format(Date())
                )
        }
    }
}



