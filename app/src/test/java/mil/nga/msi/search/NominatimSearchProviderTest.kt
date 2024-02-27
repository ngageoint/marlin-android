package mil.nga.msi.search

import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import mil.nga.msi.network.nominatim.NominatimSearchService
import mil.nga.msi.ui.map.search.NominatimResultItem
import mil.nga.msi.ui.map.search.NominatimSearchProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class NominatimSearchProviderTest {

   private lateinit var nominatimSearchService: NominatimSearchService
   private lateinit var nominatimSearchProvider: NominatimSearchProvider

   @Before
   fun setUp() {
      nominatimSearchService = mockk<NominatimSearchService>()
      nominatimSearchProvider = NominatimSearchProvider(nominatimSearchService)
   }

   @After
   fun tearDown() {
      unmockkAll()
   }

   @Test
   fun should_return_search_results() = runTest {
      val mockResponse =
         listOf(
            NominatimResultItem(
               "Washington, District of Columbia, United States",
               "38.8950368",
               "-77.0365427"
            ),
            NominatimResultItem(
               "Washington, United States",
               "47.2868352",
               "-120.212613"
            )

      )
      coEvery { nominatimSearchService.search("Test") } returns Response.success(mockResponse)

      val result = nominatimSearchProvider.search("Test")
      Assert.assertEquals(2, result.count())
      Assert.assertEquals("Washington, District of Columbia, United States", result[0].name)
      Assert.assertEquals(LatLng(38.8950368, -77.0365427), result[0].location)
      Assert.assertEquals("Washington, United States", result[1].name)
      Assert.assertEquals(LatLng(47.2868352, -120.212613), result[1].location)
   }

   @Test
   fun should_handle_invalid_lat_lon() = runTest {
      val mockResponse =
         listOf(
            NominatimResultItem(
               "Washington, District of Columbia, United States",
               "38.8950368",
               "-77.0365427"
            ),
            NominatimResultItem(
               "Washington, United States",
               "invalid lat",
               "invalid lon"
            )

      )
      coEvery { nominatimSearchService.search("Test") } returns Response.success(mockResponse)

      val result = nominatimSearchProvider.search("Test")
      Assert.assertEquals(2, result.count())
      Assert.assertEquals("Washington, District of Columbia, United States", result[0].name)
      Assert.assertEquals(LatLng(38.8950368, -77.0365427), result[0].location)
      Assert.assertEquals("Washington, United States", result[1].name)
      Assert.assertEquals(LatLng(0.0, 0.0), result[1].location)
   }
}