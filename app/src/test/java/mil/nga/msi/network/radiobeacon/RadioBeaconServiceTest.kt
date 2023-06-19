package mil.nga.msi.network.radiobeacon

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mil.nga.msi.datasource.light.PublicationVolume
import mil.nga.msi.datasource.radiobeacon.RadioBeacon
import mil.nga.msi.repository.radiobeacon.RadioBeaconLocalDataSource
import mil.nga.msi.repository.radiobeacon.RadioBeaconRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class RadioBeaconServiceTest {

    @Test
    fun testRemoteDataSource() = runTest {
        val noticesNumbers = getNoticeNumbers()

        val mockResponse = RadioBeaconResponse(
            radioBeacons = listOf(
                RadioBeacon("1", "1", "1", "01", "2023", 0.0, 0.0),
                RadioBeacon("2", "2", "2", "01", "2023", 0.0, 0.0),
            )
        )

        val mockService = mockk<RadioBeaconService>()
        coEvery {
            mockService.getRadioBeacons(
                volume = PublicationVolume.PUB_110.volumeQuery,
                output = "json",
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second,
                includeRemovals = "false"
            )
        } returns Response.success(mockResponse)

        coEvery {
            mockService.getRadioBeacons(
                volume = PublicationVolume.PUB_110.volumeQuery
            )
        } returns Response.success(mockResponse)

        val mockDataSource = mockk<RadioBeaconLocalDataSource>()
        coEvery {
            mockDataSource.getLatestRadioBeacon(volumeNumber = PublicationVolume.PUB_110.volumeTitle)
        } returns latestBeacon

        val repository = RadioBeaconRemoteDataSource(mockService, mockDataSource)
        val beacons = repository.fetchRadioBeacons(publicationVolume = PublicationVolume.PUB_110)
        assertEquals(2, beacons.size)

        coVerify {
            mockService.getRadioBeacons(
                volume = PublicationVolume.PUB_110.volumeQuery,
                minNoticeNumber = noticesNumbers.first,
                maxNoticeNumber = noticesNumbers.second,
            )
        }
    }

    companion object {
        val latestBeacon = RadioBeacon(
            id = "1",
            volumeNumber = "1",
            featureNumber = "1",
            noticeWeek = "01",
            noticeYear = "2023",
            latitude = 0.0,
            longitude =0.0
        )

        fun getNoticeNumbers(): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val week = calendar.get(Calendar.WEEK_OF_YEAR)

            val minYear = latestBeacon.noticeYear
            val minWeek = latestBeacon.noticeWeek.toInt()

            val minNoticeNumber = "${minYear}${"%02d".format(minWeek + 1)}"
            val maxNoticeNumber = "${year}${"%02d".format(week + 1)}"

            return Pair(minNoticeNumber, maxNoticeNumber)
        }
    }
}


