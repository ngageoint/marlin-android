package mil.nga.msi.network.dgpsstation

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.dgpsstation.DgpsStation
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.dgpsstations.DgpsStationResponse
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Calendar

class DgpsStationServiceTest {

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

        val mockService = mockk<DgpsStationService>()
        coEvery {
            mockService.getDgpsStations(
                volume = PublicationVolume.PUB_110.volumeQuery,
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second
            )
        } returns Response.success(mockResponse)


        coEvery {
            mockService.getDgpsStations(
                volume = PublicationVolume.PUB_110.volumeQuery
            )
        } returns Response.success(mockResponse)

        val mockDataSource = mockk<DgpsStationLocalDataSource>()
        coEvery {
            mockDataSource.getLatestDgpsStation(volumeNumber = PublicationVolume.PUB_110.volumeTitle)

        } returns latestStation

        val repository = DgpsStationRemoteDataSource(mockService, mockDataSource)
        val stations = repository.fetchDgpsStations(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, stations.size)

        coVerify {
            mockService.getDgpsStations(
                volume = PublicationVolume.PUB_110.volumeQuery,
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second
            )
        }
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

