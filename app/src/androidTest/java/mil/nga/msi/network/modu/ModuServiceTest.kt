package mil.nga.msi.network.modu

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertModusEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.modu.Modu
import mil.nga.msi.datasource.modu.RigStatus
import mil.nga.msi.repository.modu.ModuLocalDataSource
import mil.nga.msi.repository.modu.ModuRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import retrofit2.Response
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ModuServiceTest {

    private lateinit var typeAdapter: ModusTypeAdapter

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        typeAdapter = ModusTypeAdapter()
    }

    @Test
    fun testRemoteDataSource() = runTest {
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
                    sort = anyString(),
                    output = anyString(),
                    minDate = ArgumentMatchers.matches(""),
                    maxDate = ArgumentMatchers.matches("")
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
            sort = ArgumentMatchers.matches("date"),
            output = ArgumentMatchers.matches("json"),
            minDate = ArgumentMatchers.matches("2021-04-16"),
            maxDate = ArgumentMatchers.matches("2023-05-01")
        )
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(moduJson))
        val read = typeAdapter.read(jsonIn).modus

        assertEquals(1, read.size)
        assertModusEqual(modu, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        private const val moduJson = """
            {
                "modu": [{
                    "name":"590021",
                    "date":"2021-04-16",
                    "latitude":1.1999999998445219,
                    "longitude":103.53333333288998,
                    "rigStatus": "Active",
                    "specialStatus": "Specific Berth Requested",
                    "distance": 0.3,
                    "position":"53°14'00\"N \n3°14'30\"E",
                    "navArea":"HYDROLANT",
                    "region": 3,
                    "subregion":37,
                    "hostility": "Test Hostility",
                    "victim":"Test Victim",
                    "description":"Test Description"
                }]
            }"""

        private val modu = Modu(
            name = "590021",
            date = dateFormat.parse("2021-04-16")!!,
            latitude = 1.1999999998445219,
            longitude = 103.53333333288998,
        ).apply {
            rigStatus = RigStatus.ACTIVE
            specialStatus = "Specific Berth Requested"
            distance = 0.3
            position = "53°14'00\"N \n3°14'30\"E"
            navigationArea = "HYDROLANT"
            region = "3"
            subregion = "37"
        }
    }
}



