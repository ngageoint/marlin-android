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
import org.mockito.ArgumentMatchers
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import org.mockito.ArgumentMatchers.anyString

@OptIn(ExperimentalCoroutinesApi::class)
class DgpsStationServiceTest {

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
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
}

