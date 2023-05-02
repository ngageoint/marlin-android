package mil.nga.msi.network.light

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.asam.Asam
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.network.dgpsstations.DgpsStationService
import mil.nga.msi.repository.asam.AsamRemoteDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationLocalDataSource
import mil.nga.msi.repository.dgpsstation.DgpsStationRemoteDataSource
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.light.LightRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import retrofit2.http.Query
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class LightServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testRemoteDataSource() = runTest {
        val noticesNumbers = getNoticeNumbers()

        val mockResponse = LightResponse(
            lights = listOf(
                Light(
                    id = "1",
                    volumeNumber = "1",
                    featureNumber = "1",
                    characteristicNumber = 1,
                    noticeWeek = "01",
                    noticeYear = "01",
                    latitude = 0.0,
                    longitude = 0.0
                ),
                Light(
                    id = "2",
                    volumeNumber = "2",
                    featureNumber = "2",
                    characteristicNumber = 2,
                    noticeWeek = "02",
                    noticeYear = "02",
                    latitude = 0.0,
                    longitude = 0.0
                )
            )
        )

        val mockService = mock<LightService>()
        whenever(
            mockService.getLights(
                volume = PublicationVolume.PUB_110.volumeQuery,
                output = "json",
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second,
                includeRemovals = "false"
            )
        ) doReturn Response.success(mockResponse)

        whenever(
            mockService.getLights(
                volume = PublicationVolume.PUB_110.volumeQuery
            )
        ) doReturn Response.success(mockResponse)

        val mockDataSource = mock<LightLocalDataSource> {
            onBlocking {
                getLatestLight(volumeNumber = PublicationVolume.PUB_110.volumeTitle)
            } doReturn latestLight
        }

        val repository = LightRemoteDataSource(mockService, mockDataSource)
        val lights = repository.fetchLights(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, lights.size)

        verify(mockService).getLights(
            volume = PublicationVolume.PUB_110.volumeQuery,
            minNoticeNumber = noticesNumbers.first,
            maxNoticeNumber = noticesNumbers.second,
        )
    }

    companion object {
        val latestLight = Light(
            id = "1",
            volumeNumber = "1",
            featureNumber = "1",
            characteristicNumber = 1,
            noticeWeek = "34",
            noticeYear = "2011",
            latitude = 0.0,
            longitude = 0.0
        )

        fun getNoticeNumbers(): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)

            val minYear = latestLight.noticeYear
            val minWeek = latestLight.noticeWeek.toInt()

            val minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
            val maxNoticeNumber = "${year}${"%02d".format(week + 1)}"

            return Pair(minNoticeNumber, maxNoticeNumber)
        }
    }
}


