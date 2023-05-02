package mil.nga.msi.network.dgpsstation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.dgpsstations.DgpsStationResponse
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class DgpsStationServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val noticesNumbers = getNoticeNumbers()

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

        val mockService = mock<DgpsStationService>()
        whenever(
            mockService.getDgpsStations(
                volume = PublicationVolume.PUB_110.volumeQuery,
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second
            )
        ) doReturn Response.success(mockResponse)

        whenever(
            mockService.getDgpsStations(
                volume = PublicationVolume.PUB_110.volumeQuery
            )
        ) doReturn Response.success(mockResponse)

        val mockDataSource = mock<DgpsStationLocalDataSource> {
            onBlocking {
                getLatestDgpsStation(volumeNumber = PublicationVolume.PUB_110.volumeTitle)
            } doReturn latestStation
        }

        val repository = DgpsStationRemoteDataSource(mockService, mockDataSource)
        val stations = repository.fetchDgpsStations(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, stations.size)

        verify(mockService).getDgpsStations(
            volume = PublicationVolume.PUB_110.volumeQuery,
            minNoticeNumber = noticesNumbers.first,
            maxNoticeNumber = noticesNumbers.second
        )
    }

    companion object {
        val latestStation = DgpsStation(
            id = "1",
            volumeNumber = "1.0",
            featureNumber = 1f,
            noticeWeek = "34",
            noticeYear = "2011",
            latitude = 1.0,
            longitude = 1.0
        )

        fun getNoticeNumbers(): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)

            val minYear = latestStation.noticeYear
            val minWeek = latestStation.noticeWeek.toInt()

            val minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
            val maxNoticeNumber = "${year}${"%02d".format(week + 1)}"

            return Pair(minNoticeNumber, maxNoticeNumber)
        }
    }
}

