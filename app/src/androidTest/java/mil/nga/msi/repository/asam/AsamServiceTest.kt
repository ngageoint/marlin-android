package mil.nga.msi.repository.asam

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertAsamsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.network.asam.AsamResponse
import mil.nga.msi.network.asam.AsamService
import mil.nga.msi.network.asam.AsamsTypeAdapter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AsamServiceTest {

    lateinit var typeAdapter: AsamsTypeAdapter

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        typeAdapter = AsamsTypeAdapter()
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
        val asams = repository.fetchAsams().asams
        assertEquals(2, asams.size)
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(asamJson))
        val read = typeAdapter.read(jsonIn).asams

        assertEquals(1, read.size)
        assertAsamsEqual(asam, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }
}

val dateFormat = SimpleDateFormat("yyyy-MM-dd")

const val asamJson = """
{
    "asam": [{
        "reference":"2022-200",
        "date":"2022-09-07",
        "latitude":1.1999999998445219,
        "longitude":103.53333333288998,
        "position":"1°12'00\"N \n103°32'00\"E",
        "navArea":"XI",
        "subreg":"71",
        "hostility": "Test Hostility",
        "victim":"Test Victim",
        "description":"Test Description"
    }]
}"""

val asam = Asam(
    reference = "2022-200",
    date = dateFormat.parse("2022-09-07")!!,
    latitude = 1.1999999998445219,
    longitude = 103.53333333288998,
).apply {
    position = "1°12'00\"N \n103°32'00\"E"
    navigationArea = "XI"
    subregion = "71"
    hostility = "Test Hostility"
    victim = "Test Victim"
    description = "Test Description"
}


