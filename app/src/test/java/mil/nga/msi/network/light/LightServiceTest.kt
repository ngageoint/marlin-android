package mil.nga.msi.network.light

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.light.Light
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.repository.light.LightLocalDataSource
import mil.nga.msi.repository.light.LightRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Calendar

class LightServiceTest {

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

        val mockService = mockk<LightService>()
        coEvery {
            mockService.getLights(
                volume = PublicationVolume.PUB_110.volumeQuery,
                output = "json",
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second,
                includeRemovals = "false"
            )
        } returns Response.success(mockResponse)

        coEvery {
            mockService.getLights(
                volume = PublicationVolume.PUB_110.volumeQuery
            )
        } returns Response.success(mockResponse)


        val mockDataSource = mockk<LightLocalDataSource>()
        coEvery {
            mockDataSource.getLatestLight(volumeNumber = PublicationVolume.PUB_110.volumeTitle)
        } returns latestLight

        val repository = LightRemoteDataSource(mockService, mockDataSource)
        val lights = repository.fetchLights(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, lights.size)

        coVerify {
            mockService.getLights(
                volume = PublicationVolume.PUB_110.volumeQuery,
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second
            )
        }
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


