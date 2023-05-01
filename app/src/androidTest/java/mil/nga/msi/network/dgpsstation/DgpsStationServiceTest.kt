package mil.nga.msi.network.dgpsstation

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertDgpsStationsEqual
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.dgpsstations.DgpsStationResponse
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.network.dgpsstations.DgpsStationsTypeAdapter
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.io.StringReader
import org.mockito.ArgumentMatchers.anyString


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DgpsStationServiceTest {

    private lateinit var typeAdapter: DgpsStationsTypeAdapter

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        typeAdapter = DgpsStationsTypeAdapter()
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val latestStation = DgpsStation(
            id = "1",
            volumeNumber = "1.0",
            featureNumber = 1f,
            noticeWeek = "01",
            noticeYear = "23",
            latitude = 1.0,
            longitude = 1.0
        )

        val mockResponse = DgpsStationResponse(
            dgpsStations = listOf(
                DgpsStation(
                    id = "1",
                    volumeNumber = "1.0",
                    featureNumber = 1f,
                    noticeWeek = "01",
                    noticeYear = "23",
                    latitude = 1.0,
                    longitude = 1.0
                ),
                DgpsStation(
                    id = "3",
                    volumeNumber = "3.0",
                    featureNumber = 3f,
                    noticeWeek = "01",
                    noticeYear = "23",
                    latitude = 3.0,
                    longitude = 3.0
                )
            )
        )

        val mockService = mock<DgpsStationService> {
            onBlocking {
                getDgpsStations(
                    volume = ArgumentMatchers.matches(PublicationVolume.PUB_110.volumeQuery),
                    output = anyString(),
                    minNoticeNumber = anyString(),
                    maxNoticeNumber = anyString(),
                    includeRemovals = anyString()
                )
            } doReturn Response.success(mockResponse)
        }

        val mockDataSource = mock<DgpsStationLocalDataSource> {
            onBlocking {
                getLatestDgpsStation(volumeNumber = PublicationVolume.PUB_110.volumeQuery)
            } doReturn latestStation
        }

        val repository = DgpsStationRemoteDataSource(mockService, mockDataSource)
        val stations = repository.fetchDgpsStations(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, stations.size)
    }

    @Test
    fun testTypeAdapter() {
        val jsonIn = JsonReader(StringReader(dgpsStationJson))
        val read = typeAdapter.read(jsonIn).dgpsStations

        assertEquals(1, read.size)
        assertDgpsStationsEqual(dgpsStation, read[0])
        assertEquals(jsonIn.peek(), JsonToken.END_DOCUMENT)

        jsonIn.close()
    }

    companion object {
        const val dgpsStationJson = """
            {
                "ngalol": [{
                    "volumeNumber":"PUB 112",
                    "featureNumber":6.0,
                    "longitude":103.53333333288998,
                    "noticeWeek":"34",
                    "noticeYear":"2011",
                    "latitude": 1.0,
                    "longitude": 1.0,
                    "noticeNumber":201134,
                    "name": "Chojin Dan Lt",
                    "aidType":"Differential GPS Stations",
                    "position":"38°33'09\"N \n128°23'53.99\"E",
                    "geopoliticalHeading": "KOREA",
                    "regionHeading": "regionalHeading",
                    "precedingNote": "precedingNote",
                    "stationId":"T670\nR740\nR741\n",
                    "range":100,
                    "frequency":292.0,
                    "transferRate":200,
                    "remarks":"Message types: 3, 5, 7, 9, 16.",
                    "postNote":"post note",
                    "removeFromList":"N",
                    "deleteFlag":"N"
                }]
            }"""

        val dgpsStation = DgpsStation(
            id = "PUB 112--6.0",
            volumeNumber = "PUB 112",
            featureNumber = 6.0f,
            noticeWeek = "34",
            noticeYear = "2011",
            latitude = 38.55249,
            longitude = 128.39833
        ).apply {
            noticeNumber = 201134
            name = "Chojin Dan Lt"
            aidType = "Differential GPS Stations"
            position = "38°33'09\"N \n128°23'53.99\"E"
            geopoliticalHeading = "KOREA"
            regionHeading = "regionalHeading"
            precedingNote = "precedingNote"
            stationId = "T670\nR740\nR741\n"
            range = 100
            frequency = 292.0f
            transferRate = 200
            remarks = "Message types: 3, 5, 7, 9, 16."
            postNote = "post note"
            removeFromList = "N"
            deleteFlag = "N"

        }
    }
}

